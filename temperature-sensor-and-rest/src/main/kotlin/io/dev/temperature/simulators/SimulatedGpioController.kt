package io.dev.temperature.simulators

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.event.GpioPinListener
import com.pi4j.io.gpio.trigger.GpioTrigger
import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * Created by gigu on 05/07/2016.
 */
class SimulatedGpioController : GpioController {
    val states = mutableMapOf<String, PinState>()

    override fun setValue(value: Double, vararg pin: GpioPinAnalogOutput?) {
        throw UnsupportedOperationException()
    }

    override fun getProvisionedPins(): MutableCollection<GpioPin>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(milliseconds: Long, vararg pin: GpioPinDigitalOutput?) {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogInputPin(provider: GpioProvider?, pin: Pin?, name: String?): GpioPinAnalogInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogInputPin(provider: GpioProvider?, pin: Pin?): GpioPinAnalogInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogInputPin(pin: Pin?, name: String?): GpioPinAnalogInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogInputPin(pin: Pin?): GpioPinAnalogInput? {
        throw UnsupportedOperationException()
    }

    override fun isHigh(vararg pin: GpioPinDigital?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun unexportAll() {
        throw UnsupportedOperationException()
    }

    override fun addListener(listener: GpioPinListener?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun addListener(listeners: Array<out GpioPinListener>?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun setState(state: PinState?, vararg pin: GpioPinDigitalOutput?) {
        throw UnsupportedOperationException()
    }

    override fun setState(state: Boolean, vararg pin: GpioPinDigitalOutput?) {
        throw UnsupportedOperationException()
    }

    override fun addTrigger(trigger: GpioTrigger?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun addTrigger(triggers: Array<out GpioTrigger>?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun unprovisionPin(vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun isPullResistance(resistance: PinPullResistance?, vararg pin: GpioPin?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeListener(listener: GpioPinListener?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun removeListener(listeners: Array<out GpioPinListener>?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(provider: GpioProvider?, pin: Pin?, name: String?, defaultState: PinState?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(provider: GpioProvider?, pin: Pin?, defaultState: PinState?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(provider: GpioProvider?, pin: Pin?, name: String?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(provider: GpioProvider?, pin: Pin?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(pin: Pin?, name: String?, defaultState: PinState?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(pin: Pin?, defaultState: PinState?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalOutputPin(pin: Pin?, name: String?): GpioPinDigitalOutput? {
        return SimulatedGpioPin(pin!!.address, name!!)
    }

    override fun provisionDigitalOutputPin(pin: Pin?): GpioPinDigitalOutput? {
        throw UnsupportedOperationException()
    }

    override fun unexport(vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun isLow(vararg pin: GpioPinDigital?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getMode(pin: GpioPin?): PinMode? {
        throw UnsupportedOperationException()
    }

    override fun removeAllTriggers() {
        throw UnsupportedOperationException()
    }

    override fun getValue(pin: GpioPinAnalog?): Double {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(provider: GpioProvider?, pin: Pin?, name: String?, mode: PinMode?, resistance: PinPullResistance?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(provider: GpioProvider?, pin: Pin?, mode: PinMode?, resistance: PinPullResistance?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(provider: GpioProvider?, pin: Pin?, name: String?, mode: PinMode?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(provider: GpioProvider?, pin: Pin?, mode: PinMode?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(pin: Pin?, name: String?, mode: PinMode?, resistance: PinPullResistance?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(pin: Pin?, mode: PinMode?, resistance: PinPullResistance?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(pin: Pin?, name: String?, mode: PinMode?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalMultipurposePin(pin: Pin?, mode: PinMode?): GpioPinDigitalMultipurpose? {
        throw UnsupportedOperationException()
    }

    override fun low(vararg pin: GpioPinDigitalOutput?) {
        pin.forEach { states.put(it!!.name, PinState.LOW) }
    }

    override fun getPullResistance(pin: GpioPin?): PinPullResistance? {
        throw UnsupportedOperationException()
    }

    override fun getState(pin: GpioPinDigital?): PinState? {
        if (!states.containsKey(pin!!.name)) {
            states.put(pin!!.name, PinState.LOW)
        }

        return states[pin!!.name]
    }

    override fun removeTrigger(trigger: GpioTrigger?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun removeTrigger(triggers: Array<out GpioTrigger>?, vararg pin: GpioPinInput?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(options: GpioPinShutdown?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, state: PinState?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, state: PinState?, resistance: PinPullResistance?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, state: PinState?, resistance: PinPullResistance?, mode: PinMode?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun isState(state: PinState?, vararg pin: GpioPinDigital?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(provider: GpioProvider?, pin: Pin?, name: String?, resistance: PinPullResistance?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(provider: GpioProvider?, pin: Pin?, resistance: PinPullResistance?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(provider: GpioProvider?, pin: Pin?, name: String?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(provider: GpioProvider?, pin: Pin?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(pin: Pin?, name: String?, resistance: PinPullResistance?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(pin: Pin?, resistance: PinPullResistance?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(pin: Pin?, name: String?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun provisionDigitalInputPin(pin: Pin?): GpioPinDigitalInput? {
        throw UnsupportedOperationException()
    }

    override fun setMode(mode: PinMode?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(provider: GpioProvider?, pin: Pin?, name: String?, defaultValue: Double): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(provider: GpioProvider?, pin: Pin?, defaultValue: Double): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(provider: GpioProvider?, pin: Pin?, name: String?): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(provider: GpioProvider?, pin: Pin?): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(pin: Pin?, name: String?, defaultValue: Double): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(pin: Pin?, defaultValue: Double): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(pin: Pin?, name: String?): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionAnalogOutputPin(pin: Pin?): GpioPinAnalogOutput? {
        throw UnsupportedOperationException()
    }

    override fun isExported(vararg pin: GpioPin?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setPullResistance(resistance: PinPullResistance?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun isMode(mode: PinMode?, vararg pin: GpioPin?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun shutdown() {
        throw UnsupportedOperationException()
    }

    override fun high(vararg pin: GpioPinDigitalOutput?) {
        pin.forEach { states.put(it!!.name, PinState.HIGH) }
    }

    override fun toggle(vararg pin: GpioPinDigitalOutput?) {
        throw UnsupportedOperationException()
    }

    override fun export(mode: PinMode?, defaultState: PinState?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun export(mode: PinMode?, vararg pin: GpioPin?) {
        throw UnsupportedOperationException()
    }

    override fun isShutdown(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun provisionPin(provider: GpioProvider?, pin: Pin?, name: String?, mode: PinMode?, defaultState: PinState?): GpioPin? {
        throw UnsupportedOperationException()
    }

    override fun provisionPin(provider: GpioProvider?, pin: Pin?, name: String?, mode: PinMode?): GpioPin? {
        throw UnsupportedOperationException()
    }

    override fun provisionPin(provider: GpioProvider?, pin: Pin?, mode: PinMode?): GpioPin? {
        throw UnsupportedOperationException()
    }

    override fun provisionPin(pin: Pin?, name: String?, mode: PinMode?): GpioPin? {
        throw UnsupportedOperationException()
    }

    override fun provisionPin(pin: Pin?, mode: PinMode?): GpioPin? {
        throw UnsupportedOperationException()
    }

    override fun removeAllListeners() {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(provider: GpioProvider?, pin: Pin?, name: String?, defaultValue: Int): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(provider: GpioProvider?, pin: Pin?, defaultValue: Int): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(provider: GpioProvider?, pin: Pin?, name: String?): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(provider: GpioProvider?, pin: Pin?): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(pin: Pin?, name: String?, defaultValue: Int): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(pin: Pin?, defaultValue: Int): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(pin: Pin?, name: String?): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }

    override fun provisionPwmOutputPin(pin: Pin?): GpioPinPwmOutput? {
        throw UnsupportedOperationException()
    }
}
