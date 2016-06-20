package io.dev.temperature.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import kotlinx.util.with
import java.io.File

/**
 * Created by gigu on 18/06/2016.
 */
class TemperatureReadingVerticle(val w1FileLocation: String = "/sys/bus/w1/devices", val frequency: Int = 5): AbstractVerticle() {

    companion object {
        val FILE_SEARCH_PATTERN = "28-"
        val SENSOR_FILE_NAME = "w1_slave"
        val TEMP_REGEX = Regex(".*\\st=(\\d{4,5})", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    }

    override fun start(startFuture: Future<Void>?) {

    }

    fun deviceFolderPresent(): Pair<Boolean, File?> {
        val possibleFolders = File(w1FileLocation).listFiles { file, path -> path.startsWith(FILE_SEARCH_PATTERN) }
        if (possibleFolders.size == 0){
            return Pair(false, null)
        }
        return Pair(true, possibleFolders.first())
    }

    fun readTemperatureFromFile(deviceFolder: File): Float? {
        File(deviceFolder, SENSOR_FILE_NAME).with {
            val fileContent = readText()
            val match = TEMP_REGEX.find(fileContent) ?: return null

            if (match.groups.size > 1){
                val fromFile = match.groupValues.last()
                return "${fromFile.subSequence(0, 2)}.${fromFile.subSequence(2, fromFile.length)}".toFloat()
            }
        }
        return null
    }
}