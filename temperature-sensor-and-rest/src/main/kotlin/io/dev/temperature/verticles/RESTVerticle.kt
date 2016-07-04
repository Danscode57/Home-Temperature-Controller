package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.RepositoryOperations.GET_LATEST
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import java.time.Instant
import java.time.LocalDateTime

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
                    routingContext.response().putHeader("Content-Type", "application/json").
                            end(response.result().body().toJson())
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
                vertx.eventBus().send<Float>(BusAddresses.TemperatureControl.SET_TEMPERATURE, temperatureToSet, { responseMessage ->
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

        router.get("/schedule").produces("application/json").handler({ routingContext ->
            vertx.eventBus().send<JsonObject>(BusAddresses.Schedule.SCHEDULE_GET_CURRENT, JsonObject(), { resp ->
                if (resp.succeeded()) {
                    routingContext.response().putHeader("Content-Type", "application/json").
                            end(resp.result()?.body()?.encodePrettily())
                } else {
                    routingContext.response().
                            setStatusCode(500).
                            end(JsonObject().put("error", resp.cause()).encodePrettily())
                }
            })
        })

        router.post("/schedule").consumes("application/json").handler({ routingContext ->
            val scheduleRequest = routingContext.bodyAsJson
            vertx.eventBus().send<JsonObject>(BusAddresses.Schedule.SCHEDULE_SAVE, scheduleRequest, { resp ->
                if (resp.succeeded()) {
                    routingContext.response().end()
                } else {
                    routingContext.response().setStatusCode(500).putHeader("Content-Type", "application/json").
                            end(JsonObject().put("error", resp.cause()).encodePrettily())
                }
            })
        })

        router.get("/status").produces("application/json").handler({ routingContext ->
            sendSystemStatus(vertx, routingContext.response())
        })

        server.requestHandler { router.accept(it) }
        server.listen(serverPort)
    }

    private fun sendSystemStatus(vertx: Vertx, httpResponse: HttpServerResponse) {
        vertx.eventBus().send<Temperature>(BusAddresses.Repository.REPOSITORY_GET_OPERATIONS, GET_LATEST, { response ->

            val temperature = response.result().body().toJsonObject()

            vertx.eventBus().send<JsonObject>(BusAddresses.Schedule.SCHEDULE_GET_NEXT_UPDATE, JsonObject(), { resp ->

                val scheduledResponse = resp.result().body()
                val scheduleActive = scheduledResponse != null
                val nextTemperatureSetup = scheduledResponse ?: JsonObject()

                val jvmStats = jvmStatsAsJson()

                httpResponse.putHeader("Content-Type", "application/json")
                        .end(JsonObject()
                                .put("temperature", temperature)
                                .put("scheduleActive", scheduleActive)
                                .put("nextTemperature", nextTemperatureSetup)
                                .put("systemStats", jvmStats)
                                .encodePrettily())
            })
        })
    }

    private fun jvmStatsAsJson(): JsonObject {
        val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
        val runtime = Runtime.getRuntime()
        return JsonObject()
                .put("startTime", Instant.ofEpochMilli(runtimeMXBean?.startTime!!).toString())
                .put("memoryUsed", runtime?.totalMemory())
    }
}