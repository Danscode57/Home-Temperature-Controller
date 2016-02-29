package io.dev.temperature.model

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject
import java.time.Instant


data class Temperature(val value: Float, val temperatureSet: Float, val heating: Boolean, val date: Instant = Instant.now()) {
    companion object {

        fun fromJson(jsonObject: JsonObject): Temperature {
            return Temperature(
                    jsonObject.getFloat("value"),
                    jsonObject.getFloat("setTemp"),
                    jsonObject.getBoolean("heating"),
                    Instant.parse(jsonObject.getString("date")))
        }

    }

    fun toJson(): String {
        val json = toJsonObject()
        return json.encodePrettily()
    }

    fun toJsonObject(): JsonObject {
        return JsonObject().put("value", value)
                .put("setTemp", temperatureSet)
                .put("heating", heating)
                .put("date", date.toString())
    }
}

class TemperatureCodec : MessageCodec<Temperature, Temperature> {
    override fun systemCodecID(): Byte {
        return -1
    }

    override fun name(): String? {
        return Temperature::class.java.canonicalName
    }

    override fun transform(p0: Temperature?): Temperature? {
        return p0
    }

    override fun decodeFromWire(p0: Int, p1: Buffer?): Temperature? {
        val value = p1?.getFloat(p0)
        val setTemp = p1?.getFloat(p0 + 4)
        val heating: Boolean = if (p1?.getByte(p0 + 8) == Byte.MIN_VALUE ) false else true
        val date = Instant.ofEpochMilli(p1?.getLong(p0 + 9)!!)
        return Temperature(value!!, setTemp!!, heating, date)
    }

    override fun encodeToWire(p0: Buffer?, p1: Temperature?) {
        p0?.appendFloat(p1?.value!!)
        p0?.appendFloat(p1?.temperatureSet!!)
        p0?.appendByte(if (p1?.heating!!) 0 else 1)
        p0?.appendLong(p1?.date?.toEpochMilli()!!)
    }

}