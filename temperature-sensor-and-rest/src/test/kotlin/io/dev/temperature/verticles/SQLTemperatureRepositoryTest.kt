package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.dev.temperature.model.Temperature
import io.dev.temperature.model.TemperatureCodec
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(VertxUnitRunner::class)
class SQLTemperatureRepositoryTest {

    companion object {
        const val TEST_DB_NAME = "temperature.test.db"
        val vertx = Vertx.vertx()
        var jdbcClient: JDBCClient? = null

        @BeforeClass
        @JvmStatic
        fun setUp(testContext: TestContext) {
            vertx.eventBus().registerDefaultCodec(Temperature::class.java, TemperatureCodec())

            val deploymentOptionsoptions = DeploymentOptions().setWorker(true)
            val dbFile = File("${TEST_DB_NAME}.mv.db")

            if (dbFile.exists()) {
                dbFile.delete()
            }

            vertx.deployVerticle(SQLTemperatureRepository(TEST_DB_NAME), deploymentOptionsoptions, testContext.asyncAssertSuccess())

            val config = JsonObject()
                    .put("url", "jdbc:h2:./${TEST_DB_NAME}")
                    .put("driver_class", "org.h2.Driver")
                    .put("max_pool_size", 5);

            jdbcClient = JDBCClient.createShared(vertx, config)
        }
    }


    @Test fun shouldSaveEntryInTheSQLDatabaseForTemperature(context: TestContext) {
        val savedTemperature = Temperature(10.0f, 11.5f, false)
        val waitingForPersisingInDb = context.async()

        vertx.eventBus().publish(BusAddresses.Serial.TEMPERATURE_MESSAGE_PARSED, savedTemperature)
        jdbcClient?.getConnection { res ->
            val sqlConnection = res.result()
            sqlConnection.query("select * from TEMPERATURE", { queryResult ->
                if (queryResult.failed()) {
                    context.fail(queryResult.cause())
                }
                if (queryResult.result().numRows == 1) {
                    waitingForPersisingInDb.complete()
                }
            })
        }

        waitingForPersisingInDb.awaitSuccess(1000)
    }


}