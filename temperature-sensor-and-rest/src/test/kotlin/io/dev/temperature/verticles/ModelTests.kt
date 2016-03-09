package io.dev.temperature.verticles

import io.dev.temperature.model.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.junit.Assert
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import kotlin.reflect.KFunction0


class ModelTests {

    @Test fun shouldParseJsonObjectIntoTemperature() {
        val temp = Temperature(1.0f, 2.0f, true)

        val fromJson = Temperature.fromJson(temp.toJsonObject())
        assertEquals(temp, fromJson)
    }

    @Test fun shouldParseScheduleHourFromJsonObject() {
        val json = JsonObject().put("time", "12:10").put("temp", 18.5f)

        val parsedHour = ScheduleHour.fromJson(json)

        val expectedHour = ScheduleHour(LocalTime.of(12, 10), 18.5f)

        assertEquals(expectedHour, parsedHour)
    }

    @Test fun shouldConvertDayJsonToScheduleDay() {
        val jsonHour = JsonObject().put("time", "12:10").put("temp", 18.5f)
        val jsonDay = JsonObject().
                put("name", "Mon").
                put("hours", JsonArray().add(jsonHour))

        val parsedDay = ScheduleDay.fromJson(jsonDay)
        val expectedDay = ScheduleDay("Mon", listOf(ScheduleHour.fromJson(jsonHour)))

        assertEquals(expectedDay, parsedDay)
        println(parsedDay)
    }

    @Test fun shouldConvertJsonScheduleToSchedule() {
        val jsonHour = JsonObject().put("time", "12:10").put("temp", 18.5f)
        val jsonDay = JsonObject().
                put("name", "Mon").
                put("hours", JsonArray().add(jsonHour))

        val jsonSchedule = JsonObject().put("active", true).put("days", JsonArray().add(jsonDay))

        val days = listOf(ScheduleDay.fromJson(jsonDay))

        val parsedSchedule = Schedule.fromJson(jsonSchedule)
        val expectedSchedule = Schedule(true, days)

        assertEquals(expectedSchedule, parsedSchedule)
    }

    @Test fun shouldReturnNextScheduledTemperatureChangeOnTheSameDay() {
        val hours = listOf(
                ScheduleHour(LocalTime.of(11, 20), 20f),
                ScheduleHour(LocalTime.of(20, 10), 19.5f))
        val days = listOf(ScheduleDay("Mon", hours), ScheduleDay("Tue", hours))

        val schedule = Schedule(true, days)

        val nextScheduledTemp = schedule.nextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 7, 12, 0))

        assertEquals(NextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 7, 20, 10), 19.5f), nextScheduledTemp)
    }

    @Test fun shouldReturnNextScheduleTemperatureFromNextDay() {
        val hours = listOf(
                ScheduleHour(LocalTime.of(11, 20), 20f),
                ScheduleHour(LocalTime.of(20, 10), 19.5f))
        val days = listOf(ScheduleDay("Mon", hours), ScheduleDay("Tue", hours))

        val schedule = Schedule(true, days)

        val nextScheduledTemp = schedule.nextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 7, 21, 0))

        assertEquals(NextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 8, 11, 20), 20f), nextScheduledTemp)
    }

    @Test fun shouldReturnNextWeeksFirstScheduledTemperature() {
        val hours = listOf(
                ScheduleHour(LocalTime.of(11, 20), 20f),
                ScheduleHour(LocalTime.of(20, 10), 19.5f))
        val days = listOf(ScheduleDay("Mon", hours), ScheduleDay("Tue", hours))

        val schedule = Schedule(true, days)

        val nextScheduledTemp = schedule.nextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 8, 21, 0))

        assertEquals(NextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 14, 11, 20), 20f), nextScheduledTemp)
    }


    @Test fun shouldReturnNullIfThereIsNoHoursSetupInSchedule() {
        val hours = emptyList<ScheduleHour>()

        val days = listOf(ScheduleDay("Mon", hours), ScheduleDay("Tue", hours))

        val schedule = Schedule(true, days)

        val nextScheduledTemp = schedule.nextScheduledTemp(LocalDateTime.of(2016, Month.MARCH, 8, 21, 0))
        assertNull(nextScheduledTemp)
    }


    @Test fun shouldFoo() {
        val a = LocalDateTime.of(2010, Month.MARCH, 7, 11, 10)
        val b = LocalDateTime.of(2010, Month.MARCH, 7, 11, 15)

        println(a.until(b, ChronoUnit.SECONDS))
    }
}

