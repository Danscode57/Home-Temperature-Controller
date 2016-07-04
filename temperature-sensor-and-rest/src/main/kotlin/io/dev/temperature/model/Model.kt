package io.dev.temperature.model

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.eventbus.impl.codecs.JsonObjectMessageCodec
import io.vertx.core.json.JsonObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Temperature(val value: Float, val temperatureSet: Float, val heating: Boolean, val date: Instant = Instant.now()) {
    companion object {

        fun fromJson(jsonObject: JsonObject): Temperature {
            return Temperature(
                    jsonObject.getFloat("value"),
                    jsonObject.getFloat("setTemp"),
                    jsonObject.getBoolean("heating"),
                    Instant.parse(jsonObject.getString("date")))
        }

    }

    fun toJson(): String {
        val json = toJsonObject()
        return json.encodePrettily()
    }

    fun toJsonObject(): JsonObject {
        return JsonObject().put("value", value)
                .put("setTemp", temperatureSet)
                .put("heating", heating)
                .put("date", date.toString())
    }

    fun copy(): Temperature {
        return Temperature.fromJson(toJsonObject().copy())
    }
}

data class Schedule(val active: Boolean = false, val days: List<ScheduleDay>) {
    companion object {
        fun fromJson(jsonObject: JsonObject): Schedule {
            val active = jsonObject.getBoolean("active")
            val days = jsonObject.getJsonArray("days").map { ScheduleDay.fromJson(it as JsonObject) }
            return Schedule(active, days)
        }
    }

    private fun flattenListOfScheduledHoursOfDays(dateTime: LocalDateTime, tillTheEndOfWeek: List<ScheduleDay>): List<NextScheduledTemp> {
        return tillTheEndOfWeek.mapIndexed { index, scheduleDay ->
            val date = dateTime.plusDays(index.toLong()).toLocalDate()
            scheduleDay.hours.map { hour ->
                NextScheduledTemp(LocalDateTime.of(date, hour.time), hour.temp)
            }
        }.flatMap { it }
    }

    private fun dateOfNextWeekMonday(dateTime: LocalDateTime): LocalDateTime {
        val daysToAdd = 8 - dateTime.dayOfWeek.value
        return dateTime.plusDays(daysToAdd.toLong())
    }

    fun nextScheduledTemp(dateTime: LocalDateTime): NextScheduledTemp? {
        val flattenedHours = days.flatMap { it.hours }
        if (flattenedHours.size == 0) return null

        val dayIndex = dateTime.dayOfWeek.value - 1
        val tillTheEndOfWeek = days.subList(dayIndex, days.size)

        val mappedRestOfTheWeek = flattenListOfScheduledHoursOfDays(dateTime, tillTheEndOfWeek)

        val nextPossibleSchedule = mappedRestOfTheWeek.find { it.time.isAfter(dateTime) }

        if (nextPossibleSchedule == null) {
            val nextWeeksDate = dateOfNextWeekMonday(dateTime)
            return flattenListOfScheduledHoursOfDays(nextWeeksDate, days).first()
        }
        return nextPossibleSchedule
    }
}

data class ScheduleDay(val name: String, val hours: List<ScheduleHour>) {
    companion object {
        fun fromJson(jsonObject: JsonObject): ScheduleDay {
            val name = jsonObject.getString("name")
            val hours = jsonObject.getJsonArray("hours").map { ScheduleHour.fromJson(it as JsonObject) }
            return ScheduleDay(name, hours)
        }
    }
}


data class ScheduleHour(val time: LocalTime, val temp: Float) {

    companion object {
        fun fromJson(jsonObject: JsonObject): ScheduleHour {
            val timeParts = jsonObject.getString("time").split(":")
            val temp = jsonObject.getFloat("temp")
            return ScheduleHour(LocalTime.of(timeParts[0].toInt(), timeParts[1].toInt()), temp)
        }
    }
}

data class NextScheduledTemp(val time: LocalDateTime, val temp: Float) {
    fun toJson(): JsonObject {
        return JsonObject()
                .put("time", time.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")))
                .put("temp", temp)
    }
}

class TemperatureCodec : MessageCodec<Temperature, Temperature> {
    val jsonCodec: JsonObjectMessageCodec = JsonObjectMessageCodec()

    override fun systemCodecID(): Byte {
        return -1
    }

    override fun name(): String? {
        return Temperature::class.java.canonicalName
    }

    override fun transform(p0: Temperature?): Temperature? {
        return p0?.copy()
    }

    override fun decodeFromWire(p0: Int, p1: Buffer?): Temperature? {
        return Temperature.fromJson(jsonCodec.decodeFromWire(p0, p1))
    }

    override fun encodeToWire(p0: Buffer?, p1: Temperature?) {
        jsonCodec.encodeToWire(p0, p1?.toJsonObject())
    }

}