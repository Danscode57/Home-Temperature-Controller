package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.RepositoryOperations.GET_LATEST
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class CurrentStatePersister(val persistingFileName: String = "./application-state.json") : AbstractVerticle() {
    val log: Logger = LoggerFactory.getLogger(CurrentStatePersister::class.java)

    val fileWhereStateIs = File(persistingFileName)
    var currentReading: Temperature = loadLastStateFromFile()

    fun loadLastStateFromFile(): Temperature {
        if (fileWhereStateIs.exists()) {
            val content = fileWhereStateIs.readText()
            try {
                return Temperature.fromJson(JsonObject(content))
            } catch(ex: Exception) {
                log.warn("Unable to load application state from file [${persistingFileName}]", ex)
            }
        }
        log.warn("Seems like state file doesn't exist [${fileWhereStateIs.absolutePath}]")
        return Temperature(0f, 0f, false)
    }

    override fun start() {
        log.info("Starting Persister")

        vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureReadings.VALID_TEMPERATURE_READING_RECEIVED) {
            save(it.body())
        }

        vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureControl.SWITCHED_HEATING_ON) {
            save(it.body())
        }

        vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureControl.SWITCHED_HEATING_OFF) {
            save(it.body())
        }

        vertx.eventBus().consumer<String>(BusAddresses.Repository.REPOSITORY_GET_OPERATIONS) { message ->
            when (message.body()) {
                null -> log.error("Repository got null request, WTF!")
                GET_LATEST -> {
                    message.reply(currentReading)
                }
                else -> log.error("I received strange request ${message.body()} I don't know how to deal with")
            }
        }
    }

    fun save(temperature: Temperature): Temperature {
        currentReading = temperature
        fileWhereStateIs.writeText(currentReading.toJson())
        return currentReading
    }
}