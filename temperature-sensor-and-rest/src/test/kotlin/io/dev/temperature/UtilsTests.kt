package io.dev.temperature

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by gigu on 28/06/2016.
 */
class UtilsTests {

    @Test fun shouldRoundDownForValuesLessThanHalf(){
        assertEquals(18f, Utils.Temperature.roundTemperatureReading(18.2f))
        assertEquals(18.5f, Utils.Temperature.roundTemperatureReading(18.6f))
    }

    @Test fun shouldRoundUpFrValuesMoreThanHalf(){
        assertEquals(18f, Utils.Temperature.roundTemperatureReading(17.9f))
        assertEquals(18.5f, Utils.Temperature.roundTemperatureReading(18.3f))
    }


    @Test fun shouldFormatGPIOPins(){
        assertEquals("GPIO_01", "GPIO_%02d".format(1))
        assertEquals("GPIO12", "GPIO%02d".format(12 ))
    }
}