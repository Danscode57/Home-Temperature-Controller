package io.dev.temperature

object BusAddresses {

    object Serial {
        const val COMMUNICATION_RECEIVED: String = "/serialport/received"
        const val TEMPERATURE_SET_CONFIRMED: String = "/serialport/temperature-set-confirmed"
        const val TEMPERATURE_MESSAGE_PARSED: String = "/temperature/message-parsed"
        const val SET_TEMPERATURE_IN_ARDUINO: String = "/temperature/set-in-arduino"
    }

    object TemperatureControl {
        const val SET_TEMPERATURE: String = "/control/set-temperature"
    }

    object TemperatureReadings {
        const val TEMPERATUR_READING_RECEIVED: String = "/temperature/received"
        const val VALID_TEMPERATUR_READING_RECEIVED: String = "/temperature/received-valid"
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