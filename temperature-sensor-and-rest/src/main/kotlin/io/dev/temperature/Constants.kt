package io.dev.temperature

object BusAddresses {

    object TemperatureControl {
        const val SET_TEMPERATURE: String = "/control/set-temperature"
        const val SWITCHED_HEATING_ON: String = "/control/switched-heating-on"
        const val SWITCHED_HEATING_OFF: String = "/control/switched-heating-off"
    }

    object TemperatureReadings {
        const val TEMPERATURE_READING_RECEIVED: String = "/temperature/received"
        const val VALID_TEMPERATURE_READING_RECEIVED: String = "/temperature/received-valid"
        const val TEMPERATURE_SENSOR_READING_FAILED: String = "/temperature/sensor_failed"
    }

    object Repository {
        const val REPOSITORY_GET_OPERATIONS: String = "/repository/operations"
        const val REPOSITORY_GET_ALL_OPERATIONS: String = "/repository/operations-get-all"
    }

    object Schedule {
        const val SCHEDULE_GET_CURRENT: String = "/schedule/get-current"
        const val SCHEDULE_SAVE: String = "/schedule/save"
        const val SCHEDULE_UPDATED: String = "/schedule/updated"
        const val SCHEDULE_GET_NEXT_UPDATE: String = "/schedule/get-next-update"
    }
}

object RepositoryOperations {
    const val GET_LATEST = "GET_LATEST"
}

object Configuration {
    const val SIMULATED_HARDWARE = "simulatedHardware"
    const val INITIAL_TEMP = "initialTemperature"

    const val SENSORS_DIRECTORY = "sensorsDirectory"

    const val GPIO_PINS_HEATING = "gpioHeating"
    const val GPIO_PINS_INDICATOR = "gpioIndicator"
}