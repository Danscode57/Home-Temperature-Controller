package io.dev.temperature

/**
 * Created by gigu on 28/06/2016.
 */

object Utils {
    object Temperature {
        fun roundTemperatureReading(reading: Float): Float {
            return Math.round(reading / 0.5f) * 0.5f
        }
    }
}