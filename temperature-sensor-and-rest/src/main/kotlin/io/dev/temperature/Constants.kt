package io.dev.temperature

object BusAddresses {

    object Serial {
        const val COMMUNICATION_RECEIVED: String = "/serialport/received"
        const val TEMPERATURE_SET_CONFIRMED: String = "/serialport/temperature-set-confirmed"
        const val TEMPERATURE_MESSAGE_PARSED: String = "/temperature/message-parsed"
        const val SET_TEMPERATURE_IN_ARDUINO: String = "/temperature/set-in-arduino"
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