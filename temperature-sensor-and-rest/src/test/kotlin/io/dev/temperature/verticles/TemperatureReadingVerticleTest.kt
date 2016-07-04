package io.dev.temperature.verticles

import io.dev.temperature.BusAddresses
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.*
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by gigu on 18/06/2016.
 */
@RunWith(VertxUnitRunner::class)
class TemperatureReadingVerticleTest {

    companion object {
        val TEST_PATH_LOCATION = "~/tmp/temptests"
        var DEVICE_NAME_FOLDER = "28-8000001eaad4"

        val vertx = Vertx.vertx()
        val WORKER_VERTICLE = DeploymentOptions().setWorker(true)

        @BeforeClass @JvmStatic fun setUp() {
            File(TEST_PATH_LOCATION).mkdirs()
        }

        @AfterClass @JvmStatic fun tearDown() {
            File(TEST_PATH_LOCATION).deleteRecursively()
        }
    }

    @Before
    fun makeSureFoldersAreCleared() {
        File(TEST_PATH_LOCATION).listFiles().forEach { it.deleteRecursively() }
        vertx.deploymentIDs().forEach { vertx.undeploy(it) }
    }

    @Test fun shouldReturnFilePathIfLocationIsValid() {
        File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER).mkdirs()

        val verticle = TemperatureReadingVerticle(w1FileLocation = TEST_PATH_LOCATION)
        val (result, file) = verticle.deviceFolderPresent()

        assert(result)
    }

    @Test fun shouldReturnFalseIfDeviceFolderIsNotInPath() {
        val verticle = TemperatureReadingVerticle(w1FileLocation = TEST_PATH_LOCATION)
        val (result, file) = verticle.deviceFolderPresent()
        assert(!result)
    }

    @Test fun shouldReadTemperatureFromFile() {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        val sensorFile = File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME)
        sensorFile.writeText(SAMPLE_VALID_FILE_CONTENT)

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        val result = verticle.readTemperatureFromFile(sensorFile)

        Assert.assertNotNull(result)
        Assert.assertEquals(20.437f, result!!.component1())
        Assert.assertEquals("6f", result.component2())
    }

    @Test(expected = FileNotFoundException::class) fun shouldFailIfFileDoesntExists() {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        val result = verticle.readTemperatureFromFile(directory)

        Assert.assertNull(result)
    }

    @Test fun shouldReturnNullIfBoloxInFile() {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        val sensorFile = File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME)

        sensorFile.writeText("BALLS some lore ipsum shit and stuff")

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        val result = verticle.readTemperatureFromFile(sensorFile)
        Assert.assertNull(result)
    }

    @Test fun shouldFailWhenSensorFolderIsNotPresent(context: TestContext) {
        val waitingForFailedDeployment = context.async()

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        vertx.deployVerticle(verticle, WORKER_VERTICLE) { deploymentResult ->
            context.assertFalse(deploymentResult.succeeded())
            context.assertEquals(deploymentResult.cause().message, "Cannot find sensor Folders and files in location ${TEST_PATH_LOCATION}")

            waitingForFailedDeployment.complete()
        }

        waitingForFailedDeployment.await(500)
    }

    @Test fun shouldFailWhenSensorFileIsNotPresent(context: TestContext) {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()
        val waitingForFailedDeployment = context.async()

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        vertx.deployVerticle(verticle, WORKER_VERTICLE) { deploymentResult ->
            context.assertFalse(deploymentResult.succeeded())
            println(deploymentResult.result())
            context.assertTrue(deploymentResult.cause().message!!.contains("Sensor file doesn't exist "))

            waitingForFailedDeployment.complete()
        }
        waitingForFailedDeployment.await(500)
    }

    @Test fun shouldSucceedWhenSensorFileIsPresent(context: TestContext) {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        val file = File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME)
        file.writeText(SAMPLE_VALID_FILE_CONTENT)


        val waitingForFailedDeployment = context.async()
        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)
        vertx.deployVerticle(verticle, WORKER_VERTICLE) { result ->
            context.assertTrue(result.succeeded())
            waitingForFailedDeployment.complete()
        }

        waitingForFailedDeployment.await(500)
    }

    @Test fun shouldReadTemperatureContentAndSendEvent(context: TestContext) {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        val file = File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME)
        file.writeText(SAMPLE_VALID_FILE_CONTENT)


        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION, 0.1f)
        val asyncAssertSuccess = context.asyncAssertSuccess<String>()
        val waitingForMessage = context.async()

        vertx.eventBus().consumer<JsonObject>(BusAddresses.TemperatureReadings.TEMPERATURE_READING_RECEIVED) { readingMessage ->
            val jsonObject = readingMessage.body()
            context.assertEquals(jsonObject.getFloat("temperature"), 20.437f)
            context.assertEquals(jsonObject.getString("stamp"), "6f")

            waitingForMessage.complete()
        }

        vertx.deployVerticle(verticle, WORKER_VERTICLE, asyncAssertSuccess)
        waitingForMessage.await(500)

    }


    @Test fun shouldSendSensorFailEvent(context: TestContext) {
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        val sensorFile = File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME)
        sensorFile.writeText(SAMPLE_VALID_FILE_CONTENT)


        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION, 0.1f)
        val asyncAssertSuccess = context.asyncAssertSuccess<String>()
        val waitingForFirstMessage = context.async()
        val waitinfForFailMessage = context.async()

        vertx.eventBus().consumer<JsonObject>(BusAddresses.TemperatureReadings.TEMPERATURE_READING_RECEIVED) { message ->
            waitingForFirstMessage.complete()
            sensorFile.delete()
        }

        vertx.eventBus().consumer<JsonObject>(BusAddresses.TemperatureReadings.TEMPERATURE_SENSOR_READING_FAILED) { message ->
            context.assertEquals(message.body().getInteger("code"), 0)
            context.assertNotNull(message.body().getString("message"))
            waitinfForFailMessage.complete()
        }

        vertx.deployVerticle(verticle, WORKER_VERTICLE, asyncAssertSuccess)
        waitingForFirstMessage.await(500)
        waitinfForFailMessage.await(1500)


    }

    val SAMPLE_VALID_FILE_CONTENT = """47 01 ff ff 7f ff ff ff 6f : crc=6f YES
47 01 ff ff 7f ff ff ff 6f t=20437"""


}