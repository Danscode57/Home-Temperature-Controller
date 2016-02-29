package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun parseSetTemperatureMessage(messageBody: String): Float {
    val parts = messageBody.split(":")
    return parts[1].toFloat()
}

fun parseTemperatureMessage(messageBody: String): Temperature {
    val messageParts = messageBody.split(";")
    val value = messageParts[0].split(":")[1].trim().toFloat()
    val set = messageParts[1].split(":")[1].trim().toFloat()
    val heatingOn = messageParts[2].split(":")[1].trim() == "1"

    return Temperature(value, set, heating = heatingOn)
}


class MessageParsingVerticle : AbstractVerticle() {
    val log: Logger = LoggerFactory.getLogger(MessageParsingVerticle::class.java)

    override fun start() {
        log.info("Starting parsing verticle")
        vertx.eventBus().consumer<String>(BusAddresses.Serial.COMMUNICATION_RECEIVED, { handler(it) })
    }

    private fun isTemperatureMessage(firstPartOfMessage: String): Boolean {
        return firstPartOfMessage.startsWith("CURR")
    }

    private fun isSetConfirmationMessage(firstPartOfMessage: String): Boolean {
        return firstPartOfMessage.startsWith("SET")
    }

    private fun handler(message: Message<String>?) {
        val messageString = message?.body()

        when (messageString) {
            null -> log.warn("Very strange situation, receive message from serial port with no body")
            else -> {
                if (isTemperatureMessage(messageString)) {
                    val parsedTemperatureMessage = parseTemperatureMessage(messageString)
                    vertx.eventBus().publish(BusAddresses.Serial.TEMPERATURE_MESSAGE_PARSED, parsedTemperatureMessage)
                } else if (isSetConfirmationMessage(messageString)) {
                    val parsedSetConfirmation = parseSetTemperatureMessage(messageString)
                    vertx.eventBus().publish(BusAddresses.Serial.TEMPERATURE_SET_CONFIRMED, parsedSetConfirmation)
                } else {
                    log.warn("Received a message I don't understand ${messageString}")
                }
            }
        }
    }
}
