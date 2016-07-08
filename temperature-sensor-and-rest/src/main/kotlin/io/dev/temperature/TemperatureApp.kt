package io.dev.temperature

import io.dev.temperature.model.Temperature
import io.dev.temperature.model.TemperatureCodec
import io.dev.temperature.simulators.SimulatedGpioController
import io.dev.temperature.verticles.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess


class TemperatureApp : AbstractVerticle() {

    override fun start() {
        val config = config()
        log.info("--> Config ${config.encodePrettily()}")

        val simulatedHardware = config.getBoolean(Configuration.SIMULATED_HARDWARE, true)
        val deviceSensorsLocation = config.getString(Configuration.SENSORS_DIRECTORY, "/sys/bus/w1/devices")

        val workerDeploymentOptions = DeploymentOptions().setWorker(true).setConfig(config)

        vertx.eventBus().registerDefaultCodec(Temperature::class.java, TemperatureCodec())

        vertx.deployVerticle(ScheduleVerticle())

        val temperatureReadingVerticle = TemperatureReadingVerticle(w1FileLocation = deviceSensorsLocation)
        val deploymentVerticle = DeploymentVerticle(temperatureReadingVerticle)

        vertx.deployVerticle(deploymentVerticle)

        if (simulatedHardware) {
            val simulatedGpioController = SimulatedGpioController()
            vertx.deployVerticle(TemperatureController(gpioController = simulatedGpioController))
        } else {
            vertx.deployVerticle(TemperatureController())
        }
        vertx.deployVerticle(RESTVerticle(), {
            if (it.failed()) {
                log.error("Failed to start REST Verticle", it.cause())
                exitProcess(-3)
            }
        })


        vertx.deployVerticle(SQLTemperatureRepository(), workerDeploymentOptions)

    }
}

val log: Logger = LoggerFactory.getLogger(TemperatureApp::class.java)

fun main(args: Array<String>) {
    log.info("${args.joinToString { it.toString() }}")

    val vertx = Vertx.vertx()
    val jsonStringConfig = File(args[1]).readText()
    val deploymentOptions = DeploymentOptions()
    deploymentOptions.config = JsonObject(jsonStringConfig)

    vertx.deployVerticle(TemperatureApp(), deploymentOptions) {
        if (it.succeeded()) {
            log.info("!!! Started the Top Level Verticle !!! ")
        } else {
            log.error("!!! Couldn't start the Top Level Verticle", it.cause())
            exitProcess(-1)
        }
    }

}

