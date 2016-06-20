package io.dev.temperature.verticles

import io.vertx.ext.unit.junit.VertxUnitRunner
import kotlinx.util.with
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

        @BeforeClass @JvmStatic fun setUp() {
            File(TEST_PATH_LOCATION).mkdirs()
        }

        @AfterClass @JvmStatic fun tearDown() {
            File(TEST_PATH_LOCATION).deleteRecursively()
        }
    }

    @Before
    fun makeSureFoldersAreCleared(){
         File(TEST_PATH_LOCATION).listFiles().forEach { it.deleteRecursively() }
    }

    @Test fun shouldReturnFilePathIfLocationIsValid(){
        File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER).mkdirs()

        val verticle = TemperatureReadingVerticle(w1FileLocation = TEST_PATH_LOCATION)
        val (result, file) =  verticle.deviceFolderPresent()

        assert(result)
    }

    @Test fun shouldReturnFalseIfDeviceFolderIsNotInPath(){
        val verticle = TemperatureReadingVerticle(w1FileLocation = TEST_PATH_LOCATION)
        val (result, file) = verticle.deviceFolderPresent()
        assert(!result)
    }

    @Test fun shouldReadReadTemperatureFromFile(){
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME).with {
            writeText(SAMPLE_VALID_FILE_CONTENT)
        }

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        val result = verticle.readTemperatureFromFile(directory)
        Assert.assertEquals(20.437f, result)
    }

    @Test(expected = FileNotFoundException::class) fun shouldFailIfFileDoesntExists(){
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)

        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        val result = verticle.readTemperatureFromFile(directory)

        Assert.assertNull(result)
    }

    @Test fun shouldReturnNullIfBoloxInFile(){
        val directory = File(TEST_PATH_LOCATION, DEVICE_NAME_FOLDER)
        directory.mkdirs()

        File(directory, TemperatureReadingVerticle.SENSOR_FILE_NAME).with {
            writeText("BALLS some lore ipsum shit and stuff")
        }
        val verticle = TemperatureReadingVerticle(TEST_PATH_LOCATION)

        val result = verticle.readTemperatureFromFile(directory)
        Assert.assertNull(result)
    }


    val SAMPLE_VALID_FILE_CONTENT = """47 01 ff ff 7f ff ff ff 6f : crc=6f YES
47 01 ff ff 7f ff ff ff 6f t=20437"""


}