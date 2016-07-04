package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.model.Temperature
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import org.slf4j.LoggerFactory
import java.time.Instant


class SQLTemperatureRepository(val dbFilePath: String = "temperature.prod.db") : AbstractVerticle() {
    val log = LoggerFactory.getLogger(SQLTemperatureRepository::class.java)


    var jdbcClient: JDBCClient? = null
    var latestSave: Temperature? = null

    private fun initializeDB(): Future<String> {
        val config = JsonObject()
                .put("url", "jdbc:h2:./${dbFilePath}")
                .put("driver_class", "org.h2.Driver")
                .put("max_pool_size", 5);
        log.info("Using DB Config ${config.encodePrettily()}")

        jdbcClient = JDBCClient.createShared(vertx, config)

        return prepareDBForUsage()
    }

    override fun start(startFuture: Future<Void>?) {
        try {
            val initializationFuture = initializeDB()
            initializationFuture.setHandler {
                if (it.succeeded()) {
                    vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureControl.SWITCHED_HEATING_ON) { handleMessagePersistence(it) }
                    vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureControl.SWITCHED_HEATING_OFF) { handleMessagePersistence(it) }
                    vertx.eventBus().consumer<Temperature>(BusAddresses.TemperatureReadings.VALID_TEMPERATURE_READING_RECEIVED) { handleMessagePersistence(it) }

                    vertx.eventBus().consumer<JsonObject>(BusAddresses.Repository.REPOSITORY_GET_ALL_OPERATIONS, { handleGetAllReqyest(it) })

                    startFuture?.complete()
                } else startFuture?.fail(it.cause())
            }
        } catch(e: Exception) {
            log.error("Failed to start SQLite DB Verticle", e)
            startFuture?.fail(e)
        }
    }

    private fun handleMessagePersistence(message: Message<Temperature>) {
        log.debug("Received message, processing")
        jdbcClient!!.getConnection { res ->
            val connection = res.result()
            val temperature = message.body()

            if (isTemperatureReadingDifferent(latestSave, temperature!!)) {
                val insertQuery = "insert into TEMPERATURE values" +
                        " (${temperature.date.toEpochMilli()}, ${temperature.value}, ${temperature.temperatureSet}," +
                        " ${temperature.heating})"

                connection.execute(insertQuery, { insertResult ->
                    if (insertResult.succeeded()) {

                        latestSave = temperature
                        log.debug("Saved Temperature Reading in DB ${temperature}")
                    } else {
                        log.error("Unable to save Temperature ${temperature} in db", insertResult.cause())
                    }
                    connection.close()
                })
            } else connection.close()
        }
    }

    private fun handleGetAllReqyest(message: Message<JsonObject>) {
        val page = message.body().getInteger("page", 0)
        val limit = message.body().getInteger("limit", 100)

        jdbcClient!!.getConnection { res ->
            val connection = res.result()
            val selectQuery = "select date,value,setTemp,heating from TEMPERATURE order by date desc limit ${page}, ${limit}"
            log.info("Executing query [${selectQuery}]")
            connection.query(selectQuery, { result ->
                if (result.succeeded()) {
                    val returnedArray = JsonArray()
                    result.result().results.map { mapResultSetToTemperature(it).toJsonObject() }
                            .forEach { returnedArray.add(it) }

                    message.reply(returnedArray)
                } else {
                    log.error("Unable to retrieve list from the DB", result.cause())
                }
                connection.close { }
            })
        }
    }

    private fun isTemperatureReadingDifferent(left: Temperature?, right: Temperature): Boolean {

        return if (left == null) true else !(left.value == right.value
                && left.temperatureSet == right.temperatureSet
                && left.heating == right.heating)
    }

    private fun mapResultSetToTemperature(resultArray: JsonArray?): Temperature {
        return Temperature(
                resultArray!!.getFloat(1),
                resultArray.getFloat(2), resultArray.getBoolean(3),
                Instant.ofEpochMilli(resultArray.getLong(0)))
    }

    private fun prepareDBForUsage(): Future<String> {
        val dbInitializationFuture = Future.future<String>()
        jdbcClient!!.getConnection { res ->
            if (res.succeeded()) {
                val connection = res.result()
                connection.query("select * from information_schema.tables where table_name = 'TEMPERATURE'", { response ->
                    if (response.succeeded()) {
                        val result = response.result()
                        when (result?.numRows) {
                            0 -> {
                                log.info("Creating Temperature table as it is not created in the DB Yet")
                                connection.execute("create table TEMPERATURE (date BIGINT, value REAL, setTemp REAL, heating BOOLEAN)", { newTableResult ->
                                    if (newTableResult.succeeded()) {
                                        log.info("!!! New Table Temperature created !!!")
                                        dbInitializationFuture.complete("!!! New Table Temperature created !!!")
                                    } else {
                                        dbInitializationFuture.fail(newTableResult.cause())
                                    }
                                    connection.close()
                                })
                            }
                            1 -> {
                                log.info("Looks like DB Was created before. Skipping ...")
                                connection.close()
                                dbInitializationFuture.complete("Looks like DB Was created before. Skipping ...")
                            }
                            else -> {
                                log.error("Looks like there is some rubbish in the DB")
                                connection.close()
                                dbInitializationFuture.fail("There is more tables in the DB. Should be 1 only, was [${result?.numRows}]")
                            }
                        }
                    } else {
                        connection.close()
                        dbInitializationFuture.fail(response.cause())
                    }
                })

            } else {
                dbInitializationFuture.fail(res.cause())
            }
        }
        return dbInitializationFuture
    }

    override fun stop() {
        jdbcClient?.close()
    }
}