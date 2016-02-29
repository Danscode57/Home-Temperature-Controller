package io.dev.temperature.verticles

import io.dev.temperature.model.Temperature
import org.junit.Test
import org.junit.Assert.assertEquals
import kotlin.reflect.KFunction0


class ModelTests {

    @Test fun shouldParseJsonObjectIntoTemperature(){
        val temp = Temperature(1.0f, 2.0f, true)

        val fromJson = Temperature.fromJson(temp.toJsonObject())
        assertEquals(temp, fromJson)
    }
}

