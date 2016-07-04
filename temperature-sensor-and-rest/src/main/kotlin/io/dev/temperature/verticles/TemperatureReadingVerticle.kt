package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import kotlinx.util.with
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by gigu on 18/06/2016.
 */
class TemperatureReadingVerticle(val w1FileLocation: String = "/sys/bus/w1/devices", val frequency: Float = 5f) : AbstractVerticle() {
    var periodicTimer: Long = 0

    companion object {
        val FILE_SEARCH_PATTERN = "28-"
        val SENSOR_FILE_NAME = "w1_slave"
        val TEMP_REGEX = Regex(".*\\st=(\\d{4,5})", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
        val STAMP_REGEX = Regex(".*crc=(\\d|\\w{0,2})\\sYES", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    }

    override fun stop() {
        vertx.cancelTimer(periodicTimer)
    }

    override fun start(startFuture: Future<Void>?) {
        val (sensorPresent, sensorFolder) = deviceFolderPresent()

        if (!sensorPresent) {
            startFuture!!.fail(FileNotFoundException("Cannot find sensor Folders and files in location ${w1FileLocation}"))
            return
        }

        val sensorFile = File(sensorFolder, SENSOR_FILE_NAME)
        if (!sensorFile.exists()) {
            startFuture!!.fail(FileNotFoundException("Sensor file doesn't exist ${sensorFile.absolutePath}"))
            return
        }

        val delay: Long = (frequency * 1000).toLong()
        periodicTimer = vertx.setPeriodic(delay) { delay ->
            try {
                val readTemperatureFromFile = readTemperatureFromFile(sensorFile)

                if (readTemperatureFromFile != null) {
                    val receivedReading = JsonObject().
                            put("temperature", readTemperatureFromFile.component1()).
                            put("stamp", readTemperatureFromFile.component2())

                    vertx.eventBus().publish(BusAddresses.TemperatureReadings.TEMPERATURE_READING_RECEIVED, receivedReading)
                }
            } catch(exception: FileNotFoundException) {
                val message = JsonObject().put("code", 0).put("message", exception.message)
                vertx.eventBus().publish(BusAddresses.TemperatureReadings.TEMPERATURE_SENSOR_READING_FAILED, message)
            }
        }
        startFuture!!.complete()


    }

    fun deviceFolderPresent(): Pair<Boolean, File?> {
        val possibleFolders = File(w1FileLocation).listFiles { file, path -> path.startsWith(FILE_SEARCH_PATTERN) }
        if (possibleFolders.size == 0) {
            return Pair(false, null)
        }
        return Pair(true, possibleFolders.first())
    }

    fun readTemperatureFromFile(deviceFile: File): Pair<Float, String>? {
        deviceFile.with {
            val fileContent = readText()
            val stampMatch = STAMP_REGEX.find(fileContent) ?: return null
            val match = TEMP_REGEX.find(fileContent) ?: return null

            if (match.groups.size > 1) {
                val fromFile = match.groupValues.last()
                return Pair("${fromFile.subSequence(0, 2)}.${fromFile.subSequence(2, fromFile.length)}".toFloat(), stampMatch.groupValues.last())
            }
        }
        return null
    }
}