package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.model.NextScheduledTemp
import io.dev.temperature.model.Schedule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
    var nextTemperatureSetup: NextScheduledTemp? = null

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

        vertx.eventBus().consumer<JsonObject>(BusAddresses.Schedule.SCHEDULE_GET_NEXT_UPDATE, { message ->
            message.reply(nextTemperatureSetup?.toJson())
        })

        vertx.eventBus().consumer<JsonObject>(BusAddresses.Schedule.SCHEDULE_UPDATED, { message ->
            val newSchedule = message.body()
            if (scheduleFile == null) {
                scheduleFile = File(scheduleFilePath)
            }
            scheduleFile!!.writeText(newSchedule.encodePrettily())
            log.info("Saved new Schedule to file [${scheduleFile?.absolutePath}]")

            if (!currentTimer.equals(0)) {
                log.info("Canceling previously scheduled temperature setup")
                vertx.cancelTimer(currentTimer)
                currentTimer = 0
                nextTemperatureSetup = null
            }

            if (shouldScheduleNextJob(currentSchedule)) {
                currentTimer = scheduleNextJob(currentSchedule, vertx)
            }

        })

        if (shouldScheduleNextJob(currentSchedule)) {
            currentTimer = scheduleNextJob(currentSchedule, vertx)
        } else {
            log.info("There is no Active schedule at the moment.")
        }

        log.info("Started Schedule verticle")
    }

    private fun shouldScheduleNextJob(schedule: JsonObject?): Boolean {
        return (schedule != null && schedule.getBoolean("active", false))
    }

    private fun scheduleNextJob(scheduleJson: JsonObject?, vertx: Vertx): Long {
        val schedule = Schedule.fromJson(scheduleJson!!)
        val now = LocalDateTime.now()
        val nextScheduledTemp = schedule.nextScheduledTemp(now) ?: return 0
        nextTemperatureSetup = nextScheduledTemp

        val delay = now.until(nextScheduledTemp.time, ChronoUnit.MILLIS)

        log.info("Scheduling next temperature setup [${nextScheduledTemp}]")

        return vertx.setTimer(delay, {
            vertx.eventBus().send<Float>(BusAddresses.TemperatureControl.SET_TEMPERATURE, nextScheduledTemp.temp, { responseMessage ->
                if (responseMessage.failed()) {
                    log.error("Unable to send new temperature setup", responseMessage.cause())
                }
                currentTimer = scheduleNextJob(currentSchedule, vertx)
            })
        })
    }

}