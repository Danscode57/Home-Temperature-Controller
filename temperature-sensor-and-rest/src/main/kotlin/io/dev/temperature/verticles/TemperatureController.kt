package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * Created by gigu on 22/06/2016.
 */
class TemperatureController(val initialTemperature: Float = 18.5f) : AbstractVerticle() {
    val log = LoggerFactory.getLogger("Temperature Controller")

    var lastTemperatureReading: Float = Float.MAX_VALUE
    var lastTemperatureReadingStamp: String = ""
    var lastTemperatureReadingTime: Long = 0
    var setTemperature: Float = initialTemperature
    var heatingOn: Boolean = false

    override fun start() {

        vertx.eventBus().consumer<JsonObject>(BusAddresses.TemperatureReadings.TEMPERATUR_READING_RECEIVED) { message ->
            val jsonObject = message.body()
            if (jsonObject != null) {
                val temperature = jsonObject.getFloat("temperature")
                val readStamp = jsonObject.getString("stamp")
                processTemperatureReading(temperature, readStamp)
            } else {
                log.warn("Received temperature reading with empty message")

            }
        }

        vertx.eventBus().consumer<Float>(BusAddresses.TemperatureControl.SET_TEMPERATURE) { message ->
            if (message.body() != null){
                setTemperature = message.body()
                message.reply(null)
            } else {
                message.fail(405, "Request set temperature doesn't have temperature value")
            }
        }
    }

    fun processTemperatureReading(temperature: Float, stamp: String) {
        if (stamp != lastTemperatureReadingStamp) {
            lastTemperatureReadingStamp = stamp
            lastTemperatureReading = temperature
            lastTemperatureReadingTime = Instant.now().toEpochMilli()
        }
        Temperature(lastTemperatureReading, setTemperature, heatingOn, Instant.now())
        vertx.eventBus().publish(BusAddresses.TemperatureReadings.VALID_TEMPERATUR_READING_RECEIVED, null)
    }

}


