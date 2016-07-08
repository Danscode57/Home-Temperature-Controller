package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

/**
 * Created by gigu on 08/07/2016.
 */
class DeploymentVerticle(val temperatureReadingVerticle: TemperatureReadingVerticle, val deploymentRetryDelayInSec: Long = 10, val numberOfAcceptedFailedDeployments: Int = 5) : AbstractVerticle() {
    val log = LoggerFactory.getLogger(DeploymentVerticle::class.java)
    var deployment: String? = null
    var failedReading: Int = 0

    override fun start() {
        vertx.eventBus().consumer<String>(BusAddresses.TemperatureReadings.TEMPERATURE_SENSOR_READING_FAILED) { message ->
            if (failedReading > numberOfAcceptedFailedDeployments) {
                undeployVerticle()
            } else {
                log.warn("Problems with sensor? Problem number $failedReading. Message [${message.body()}]")
                failedReading++
            }
        }

        vertx.eventBus().consumer<String>(BusAddresses.TemperatureReadings.DEPLOYMENT_FAILED) { message ->
            vertx.setTimer(1000 * deploymentRetryDelayInSec) { attemptDeployment() }
        }

        vertx.eventBus().consumer<Any>(BusAddresses.TemperatureReadings.TEMPERATURE_READING_RECEIVED) { anything ->
            failedReading = 0
        }

        attemptDeployment()
    }

    private fun undeployVerticle() {
        log.info("Undeploying temperature verticle $deployment")
        vertx.undeploy(deployment) { result ->
            if (result.succeeded()) {
                val message = "Verticle ${deployment} undeployed"
                log.info(message)
                deployment = null
                failedReading = 0
                vertx.eventBus().publish(BusAddresses.TemperatureReadings.DEPLOYMENT_FAILED, message)

            } else {
                log.error("Unable to undeploy", result.cause())
            }
        }
    }

    private fun attemptDeployment() {
        vertx.deployVerticle(temperatureReadingVerticle) { deploymentResult ->
            if (deploymentResult.succeeded()) {
                deployment = deploymentResult.result()
                log.info("Temperature reading Verticle deployed ${deploymentResult.result()}")
                failedReading = 0
            } else {
                log.warn("Unable to deploy verticle, reason: [${deploymentResult.result()}]. Trying again in ${deploymentRetryDelayInSec} sec", deploymentResult.cause())
                vertx.eventBus().publish(BusAddresses.TemperatureReadings.DEPLOYMENT_FAILED, deploymentResult.result())
            }
        }
    }
}