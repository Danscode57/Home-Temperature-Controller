package io.dev.temperature

import io.dev.temperature.model.Temperature
import io.dev.temperature.model.TemperatureCodec
import io.dev.temperature.verticles.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess


class TemperatureApp : AbstractVerticle() {

    override fun start() {
        val serialPortConfig = config().getJsonObject("serialPort", JsonObject().put("path", "/tmp/foo"))
        val serialPortPath = serialPortConfig.getString("path", "/tmp/foo")

        vertx.eventBus().registerDefaultCodec(Temperature::class.java, TemperatureCodec())

        vertx.deployVerticle(MessageParsingVerticle())

        vertx.deployVerticle(InMemoryTemperatureRepository())

        vertx.deployVerticle(SerialPortTemperatureVerticle(serialPortPath), {
            when (it.succeeded()) {
                true -> {
                    vertx.deployVerticle(RESTVerticle(), {
                        if (it.failed()) {
                            log.error("Failed to start REST Verticle", it.cause())
                            exitProcess(-3)
                        }
                    })
                }
                false -> {
                    log.error("Unable to start Serial Port Verticle :(", it.cause())
                    exitProcess(-2)
                }

            }
        })
        val deploymentOptionsoptions = DeploymentOptions().setWorker(true)

        vertx.deployVerticle(SQLTemperatureRepository(), deploymentOptionsoptions)
    }
}

val log: Logger = LoggerFactory.getLogger(TemperatureApp::class.java)

fun main(args: Array<String>) {

    val vertx = Vertx.vertx()

    vertx.deployVerticle(TemperatureApp(), {
        if (it.succeeded()) {
            log.info("!!! Started the Top Level Verticle !!! ")
        } else {
            log.error("!!! Couldn't start the Top Level Verticle", it.cause())
            exitProcess(-1)
        }
    })

}

