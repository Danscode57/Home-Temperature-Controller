package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.RepositoryOperations.GET_LATEST
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class InMemoryTemperatureRepository : AbstractVerticle() {
    val log: Logger = LoggerFactory.getLogger(InMemoryTemperatureRepository::class.java)

    var currentReading: Temperature = Temperature(0f, 0f, heating = false)

    override fun start() {
        log.info("Starting InMemoryRepository")

        vertx.eventBus().consumer<Temperature>(BusAddresses.Serial.TEMPERATURE_MESSAGE_PARSED, {
            save(it.body())
        })

        vertx.eventBus().consumer<String>(BusAddresses.Repository.REPOSITORY_GET_OPERATIONS, { message ->
            when (message.body()) {
                null -> log.error("Repository got null request, WTF!")
                GET_LATEST -> {
                    message.reply(currentReading)
                }
                else -> log.error("I received strange request ${message.body()} I don't know how to deal with")
            }
        })
    }

     fun save(temperature: Temperature): Temperature {
        currentReading = temperature
        return currentReading
    }
}