package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.RepositoryOperations.GET_LATEST
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import org.slf4j.LoggerFactory

class RESTVerticle(val serverPort: Int = 8080) : AbstractVerticle() {

    val log = LoggerFactory.getLogger(RESTVerticle::class.java)


    override fun start() {
        log.info("Initiating REST Server")
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        router.route().handler(BodyHandler.create())

        val corsHandler = CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
                .allowedHeaders(setOf("Content-Type", "Accept", "Authentication"))

        router.route().handler(corsHandler)

        router.get("/temp").produces("application/json").handler({ routingContext ->
            vertx.eventBus().send<Temperature>(BusAddresses.Repository.REPOSITORY_GET_OPERATIONS, GET_LATEST, { response ->
                if (response.succeeded()) {
                    routingContext.response().end(response.result().body().toJson())
                } else {
                    routingContext.response().setStatusCode(500).end()
                }
            })
        })

        router.post("/temp").consumes("application/json").handler({ routingContext ->
            val temperatureToSet = routingContext.bodyAsJson.getFloat("setTemp")
            if ((temperatureToSet * 10 % 5) > 0) {
                val errorMessage = JsonObject().put("error", "setTemp can only be in 0.5 increments")
                routingContext.response().setStatusCode(400).end(errorMessage.encodePrettily())
            } else {
                vertx.eventBus().send<Float>(BusAddresses.Serial.SET_TEMPERATURE_IN_ARDUINO, temperatureToSet, { responseMessage ->
                    if (responseMessage.succeeded()) {
                        routingContext.response().setStatusCode(201).end()
                    }
                })
            }
        })

        router.get("/temp/:page/:limit").produces("application/json").handler({ routingContext ->
            val limit = routingContext.request().getParam("limit").toInt()
            val page = routingContext.request().getParam("page").toInt()
            val requestParameters = JsonObject().put("page", page).put("limit", limit)
            vertx.eventBus().send<JsonArray>(BusAddresses.Repository.REPOSITORY_GET_ALL_OPERATIONS, requestParameters, { reply ->
                if (reply.failed()) {
                    val error = JsonObject().put("error", reply.cause().message)
                    routingContext.response().setStatusCode(500).end(error.encodePrettily())
                } else {
                    routingContext.response().end(reply.result().body().encodePrettily())
                }
            })
        })

        router.get("/status").produces("application/json").handler({ routingContext ->
            //TODO: Fill with code, oh yes! System status messages!
        })

        server.requestHandler { router.accept(it) }
        server.listen(serverPort)
    }
}