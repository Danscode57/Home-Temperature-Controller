package io.dev.temperature.verticles

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.GpioPinDigitalOutput
import com.pi4j.io.gpio.RaspiPin
import io.dev.temperature.BusAddresses
import io.dev.temperature.Configuration
import io.dev.temperature.Utils
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.time.Instant


/**
 * Created by gigu on 22/06/2016.
 */
class TemperatureController(val configuration: JsonObject = JsonObject(), val gpioController: GpioController = GpioFactory.getInstance()) : AbstractVerticle() {
    val log = LoggerFactory.getLogger("Temperature Controller")

    val gpioHeatingControllerPin: Int = configuration.getInteger(Configuration.GPIO_PINS_HEATING, 0)
    val gpioHeatingIndicatorPin: Int = configuration.getInteger(Configuration.GPIO_PINS_INDICATOR, 2)

    val heatingPin: GpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO%.2d".format(gpioHeatingControllerPin)), "HeatingPin")
    val indicatorPin: GpioPinDigitalOutput = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO%.2d".format(gpioHeatingIndicatorPin)), "IndicatorPin")

    var lastTemperatureReading: Float = Float.MAX_VALUE
    var lastTemperatureReadingStamp: String = ""
    var lastTemperatureReadingTime: Long = 0
    var setTemperature: Float = configuration.getFloat(Configuration.INITIAL_TEMP, 18.5f)
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
            if (message.body() != null) {
                setTemperature = message.body()
                message.reply(null)
            } else {
                message.fail(405, "Request set temperature doesn't have temperature value")
            }
        }

        vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureReadings.VALID_TEMPERATUR_READING_RECEIVED) { message ->
            if (message.body() != null) {
                if (shouldSwitchHeatingOn()) {
                    switchHeatingOn()
                } else {
                    switchHeatingOff()
                }
            } else {
                log.warn("Received temperature reading message with no content")
            }
        }
    }

    fun processTemperatureReading(temperature: Float, stamp: String) {
        if (stamp != lastTemperatureReadingStamp) {
            val timeNow = Instant.now()
            lastTemperatureReadingStamp = stamp
            lastTemperatureReading = Utils.Temperature.roundTemperatureReading(temperature)
            lastTemperatureReadingTime = timeNow.toEpochMilli()
            val temperatureEntity = Temperature(lastTemperatureReading, setTemperature, heatingOn, timeNow)

            vertx.eventBus().publish(BusAddresses.TemperatureReadings.VALID_TEMPERATUR_READING_RECEIVED, temperatureEntity)
        }
    }

    fun shouldSwitchHeatingOn(): Boolean {
        return lastTemperatureReading < setTemperature
    }


    fun switchHeatingOn() {
        if (gpioController.getState(heatingPin).isLow){
            gpioController.high(heatingPin)
            gpioController.high(indicatorPin)
            heatingOn = true
        }
    }

    fun switchHeatingOff() {
        if (gpioController.getState(heatingPin).isHigh){
            gpioController.low(heatingPin)
            gpioController.low(indicatorPin)
            heatingOn = false
        }
    }

}


