package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant
import java.time.LocalDateTime

class ScheduleVerticle(val scheduleFilePath: String = "./schedule.json") : AbstractVerticle() {

    val log: Logger = LoggerFactory.getLogger(ScheduleVerticle::class.java)

    val EMPTY_SCHEDULE = JsonObject().
            put("active", false).
            put("days", JsonArray().
                    add(JsonObject().
                            put("name", "Mon").
                            put("hours", JsonArray())).
                    add(JsonObject().
                            put("name", "Tue").
                            put("hours", JsonArray())).
                    add(JsonObject().
                            put("name", "Wed").
                            put("hours", JsonArray())).
                    add(JsonObject().
                            put("name", "Thu").
                            put("hours", JsonArray())).
                    add(JsonObject().
                            put("name", "Fri").
                            put("hours", JsonArray())).
                    add(JsonObject().
                            put("name", "Sat").
                            put("hours", JsonArray())).
                    add(JsonObject().
                            put("name", "Sun").
                            put("hours", JsonArray()))
            )

    var currentSchedule: JsonObject? = null
    var scheduleFile: File? = null
    var currentTimer: Long = 0

    override fun start() {
        scheduleFile = File(scheduleFilePath)

        if (!scheduleFile!!.exists()) {
            scheduleFile!!.createNewFile()
            scheduleFile!!.writeText(EMPTY_SCHEDULE.encodePrettily())
        }

        currentSchedule = JsonObject(scheduleFile!!.readText())

        vertx.eventBus().consumer<JsonObject>(BusAddresses.Schedule.SCHEDULE_GET_CURRENT, { message ->
            message.reply(currentSchedule)
        })

        vertx.eventBus().consumer<JsonObject>(BusAddresses.Schedule.SCHEDULE_SAVE, { message ->
            currentSchedule = message.body()
            message.reply(currentSchedule)
            vertx.eventBus().publish(BusAddresses.Schedule.SCHEDULE_UPDATED, currentSchedule)
        })

        vertx.eventBus().consumer<JsonObject>(BusAddresses.Schedule.SCHEDULE_UPDATED, { message ->
            val newSchedule = message.body()
            if (scheduleFile == null) {
                scheduleFile = File(scheduleFilePath)
            }
            scheduleFile!!.writeText(newSchedule.encodePrettily())
            log.info("Saved new Schedule to file [${scheduleFile?.absolutePath}]")
        })



        log.info("Started Schedule verticle")
    }

    private fun shouldScheduleNextJob(schedule: JsonObject): Boolean {
        return schedule.getBoolean("active")
    }

    private fun scheduleNextJob(schedule: JsonObject, vertx: Vertx): Long {

        return 0;
    }

    private fun getNextTemperatureSetup(schedule: JsonObject): Float {
        val now = LocalDateTime.now()
        val currentDayIndex = now.dayOfWeek.value - 1

        val jsonObject = schedule.getJsonArray("days").getJsonObject(currentDayIndex)

        return 0f;
    }
}