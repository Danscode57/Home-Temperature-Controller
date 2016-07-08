package io.dev.temperature.verticles

import com.pi4j.io.gpio.*
import io.dev.temperature.BusAddresses
import io.dev.temperature.Configuration
import io.dev.temperature.RepositoryOperations
import io.dev.temperature.Utils
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant


/**
 * Created by gigu on 22/06/2016.
 */
class TemperatureController(val configuration: JsonObject = JsonObject(), val gpioController: GpioController = GpioFactory.getInstance(), val persistingFileName: String = "./application-state.json") : AbstractVerticle() {
    val log = LoggerFactory.getLogger(TemperatureController::class.java)

    val fileWhereStateIs = File(persistingFileName)
    var currentReading: Temperature = loadLastStateFromFile()

    val gpioHeatingControllerPin: Int = configuration.getInteger(Configuration.GPIO_PINS_HEATING, 0)
    val gpioHeatingIndicatorPin: Int = configuration.getInteger(Configuration.GPIO_PINS_INDICATOR, 2)

    val pinHeating = RaspiPin.getPinByName("GPIO $gpioHeatingControllerPin")
    val pinIndicator = RaspiPin.getPinByName("GPIO $gpioHeatingIndicatorPin")

    val heatingPin = prepareOutputPin(pinHeating)
    val indicatorPin = prepareOutputPin(pinIndicator)

    var lastTemperatureReadingStamp: String = ""
    var lastTemperatureReadingTime: Long = 0


    override fun start() {
        vertx.eventBus().consumer<JsonObject>(BusAddresses.TemperatureReadings.TEMPERATURE_READING_RECEIVED) { message ->
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
                val setTemperature = message.body()
                val newSetup = Temperature(currentReading.value, setTemperature, currentReading.heating, sensorOk = currentReading.sensorOk)
                save(newSetup)
                log.info("Set temperature to $newSetup")
                vertx.eventBus().publish(BusAddresses.TemperatureReadings.MODIFIED_TEMPERATURE_READING_OR_SETTING, newSetup)
                message.reply(null)
            } else {
                message.fail(405, "Request set temperature doesn't have temperature value")
            }
        }

        vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureReadings.MODIFIED_TEMPERATURE_READING_OR_SETTING) { message ->
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

        vertx.eventBus().consumer<String>(BusAddresses.Repository.REPOSITORY_GET_OPERATIONS) { message ->
            when (message.body()) {
                null -> log.error("Repository got null request, WTF!")
                RepositoryOperations.GET_LATEST -> {
                    message.reply(currentReading)
                }
                else -> log.error("I received strange request ${message.body()} I don't know how to deal with")
            }
        }

        vertx.eventBus().consumer<Any>(BusAddresses.TemperatureReadings.TEMPERATURE_SENSOR_READING_FAILED) {
            save(Temperature(currentReading.value, currentReading.temperatureSet, currentReading.heating, sensorOk = false))
        }
        vertx.eventBus().consumer<Any>(BusAddresses.TemperatureReadings.DEPLOYMENT_FAILED) {
            save(Temperature(currentReading.value, currentReading.temperatureSet, currentReading.heating, sensorOk = false))
        }
    }

    fun processTemperatureReading(temperature: Float, stamp: String) {
        log.debug("Received temperature reading $temperature with stamp $stamp")

        if (stamp != lastTemperatureReadingStamp) {
            val timeNow = Instant.now()
            lastTemperatureReadingStamp = stamp
            lastTemperatureReadingTime = timeNow.toEpochMilli()

            val temperatureEntity = Temperature(Utils.Temperature.roundTemperatureReading(temperature), currentReading.temperatureSet, currentReading.heating)
            save(temperatureEntity)

            vertx.eventBus().publish(BusAddresses.TemperatureReadings.MODIFIED_TEMPERATURE_READING_OR_SETTING, temperatureEntity)
        }
    }

    fun shouldSwitchHeatingOn(): Boolean {
        log.debug("$currentReading")
        return currentReading.value < currentReading.temperatureSet
    }


    fun switchHeatingOn() {
        try {
            if (heatingPin.isLow) {
                heatingPin.high()
                indicatorPin.high()

                val temperature = Temperature(currentReading.value, currentReading.temperatureSet, true, sensorOk = currentReading.sensorOk)

                log.info("Switched heating on")
                save(temperature)
                vertx.eventBus().publish(BusAddresses.TemperatureControl.SWITCHED_HEATING_ON, temperature)
            }
        } catch(ex: Exception) {
            log.error("Failed to switch on heating", ex)
        }
    }

    fun switchHeatingOff() {
        try {
            if (heatingPin.isHigh) {
                heatingPin.low()
                indicatorPin.low()
                val temperature = Temperature(currentReading.value, currentReading.temperatureSet, false, sensorOk = currentReading.sensorOk)

                log.info("Switched heating off")
                save(temperature)
                vertx.eventBus().publish(BusAddresses.TemperatureControl.SWITCHED_HEATING_OFF, temperature)
            }
        } catch(ex: Exception) {
            log.error("Failed to switch off heating", ex)
        }

    }

    private fun prepareOutputPin(pin: Pin): GpioPinDigitalOutput {
        val provisionedAlready = gpioController.provisionedPins.find { it.pin.name == pin.name }
        if (provisionedAlready != null) {
            log.info("--> PIN ${pin.toString()} already provisioned")
            if (provisionedAlready.isMode(PinMode.DIGITAL_OUTPUT)) {
                log.info("--> PING ${pin.toString()} provisioned and in the right mode. Using it!")
                return provisionedAlready as GpioPinDigitalOutput
            }
            log.info("--> PIN ${pin.toString()} not provisioned as output, will provision it again")
            gpioController.unprovisionPin(provisionedAlready)
        }
        val provisionDigitalOutputPin = gpioController.provisionDigitalOutputPin(pin, PinState.LOW)
        log.info("--> PIN ${provisionDigitalOutputPin.toString()} provisioned")
        return provisionDigitalOutputPin
    }

    fun loadLastStateFromFile(): Temperature {
        if (fileWhereStateIs.exists()) {
            val content = fileWhereStateIs.readText()
            try {
                val fromJson = Temperature.fromJson(JsonObject(content))
                log.info("Last application state loaded as $fromJson")
                return fromJson
            } catch(ex: Exception) {
                log.warn("Unable to load application state from file [${persistingFileName}]", ex)
            }
        }
        log.warn("Seems like state file doesn't exist [${fileWhereStateIs.absolutePath}]")
        return Temperature(18.5f, 18.5f, false)
    }

    fun save(temperature: Temperature): Temperature {
        currentReading = temperature
        fileWhereStateIs.writeText(currentReading.toJson())
        return currentReading
    }
}


