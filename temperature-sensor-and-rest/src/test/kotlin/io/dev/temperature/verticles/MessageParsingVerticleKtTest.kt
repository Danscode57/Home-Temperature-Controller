package io.dev.temperature.verticles

import io.dev.temperature.model.Temperature
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant


class MessageParsingVerticleKtTest {

    @Test
    fun testParseSetTemperatureMessage() {
        assertEquals(19.5f, parseSetTemperatureMessage("SET:19.5"))
        assertEquals(19.0f, parseSetTemperatureMessage("SET:19"))
    }

    @Test fun shouldParseTemperatureMessage(){
        val date = Instant.now()
        val expected = Temperature(10.0f, 20.0f, true, date)

        val actual = parseTemperatureMessage("CURR:10.0;SET:20.0;HEATING:true;READING:10.5")

        assertEquals(expected.value, actual.value)
        assertEquals(expected.temperatureSet, actual.temperatureSet)
        assertEquals(expected.heating, actual.heating)
    }
}