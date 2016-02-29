package io.dev.temperature.verticles

import gnu.io.RXTXPort
import gnu.io.SerialPortEvent
import gnu.io.SerialPortEventListener
import io.dev.temperature.BusAddresses
import io.dev.temperature.log
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream


class SerialPortTemperatureVerticle(val serialPortAddress: String) : AbstractVerticle() {
    val log = LoggerFactory.getLogger(SerialPortTemperatureVerticle::class.java)

    override fun start(startFuture: Future<Void>?) {
        try {
            log.info("Starting Serial Port Verticle, using location [${serialPortAddress}]")
            val serialPort = RXTXPort(serialPortAddress)

            serialPort.addEventListener(SerialReader(vertx.eventBus(), serialPort.inputStream))
            SerialWriter(serialPort.outputStream, vertx.eventBus())

            serialPort.notifyOnDataAvailable(true)

            startFuture?.complete()
        } catch(exception: Exception) {
            startFuture?.fail(exception)
        }
    }

    class SerialReader(val eventBus: EventBus, val inputStream: InputStream) : SerialPortEventListener {
        override fun serialEvent(serialPortEvent: SerialPortEvent?) {
            val buffer = Array<Byte>(1024, { i -> 0 })
            var data: Int
            var len = 0

            do {
                data = inputStream.read()

                if (data.toChar() == '\n') {
                    break
                }
                buffer[len++] = data.toByte()
            } while (data > -1)

            val stringFromSerialPort = java.lang.String(buffer.toByteArray(), 0, len)
            log.debug("Received data", serialPortEvent)
            eventBus.publish(BusAddresses.Serial.COMMUNICATION_RECEIVED, stringFromSerialPort)
        }

    }

    class SerialWriter(val outputStream: OutputStream, val eventBus: EventBus) {

        init {
            eventBus.consumer<Float>(BusAddresses.Serial.SET_TEMPERATURE_IN_ARDUINO, { message ->
                val temperatureToSet = message.body()
                writeToPort(temperatureToSet)

                message.reply(temperatureToSet)
            })
        }

        fun writeToPort(temperatureSet: Float) {
            try {
                outputStream.write("${temperatureSet}\n".toByteArray())
                outputStream.flush()
            } catch(e: Exception) {
                log.error("Unable to send temperature to the Serial port", e)
            }
        }
    }
}