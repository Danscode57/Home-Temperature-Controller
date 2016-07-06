package io.dev.temperature.simulators

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.event.GpioPinListener
import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * Created by gigu on 05/07/2016.
 */

class SimulatedGpioPin(val ping: Int, val pinName: String) : GpioPinDigitalOutput {
    override fun getPin(): Pin? {
        throw UnsupportedOperationException()
    }

    override fun getName(): String? {
        return pinName
    }

    override fun getListeners(): MutableCollection<GpioPinListener>? {
        throw UnsupportedOperationException()
    }

    override fun addListener(vararg listener: GpioPinListener?) {
        throw UnsupportedOperationException()
    }

    override fun addListener(listeners: MutableList<out GpioPinListener>?) {
        throw UnsupportedOperationException()
    }

    override fun hasListener(vararg listener: GpioPinListener?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getProperties(): MutableMap<String, String>? {
        throw UnsupportedOperationException()
    }

    override fun getProperty(key: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun getProperty(key: String?, defaultValue: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun isPullResistance(resistance: PinPullResistance?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeListener(vararg listener: GpioPinListener?) {
        throw UnsupportedOperationException()
    }

    override fun removeListener(listeners: MutableList<out GpioPinListener>?) {
        throw UnsupportedOperationException()
    }

    override fun unexport() {
        throw UnsupportedOperationException()
    }

    override fun getMode(): PinMode? {
        throw UnsupportedOperationException()
    }

    override fun getTag(): Any? {
        throw UnsupportedOperationException()
    }

    override fun getPullResistance(): PinPullResistance? {
        throw UnsupportedOperationException()
    }

    override fun hasProperty(key: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(options: GpioPinShutdown?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, state: PinState?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, state: PinState?, resistance: PinPullResistance?) {
        throw UnsupportedOperationException()
    }

    override fun setShutdownOptions(unexport: Boolean?, state: PinState?, resistance: PinPullResistance?, mode: PinMode?) {
        throw UnsupportedOperationException()
    }

    override fun setMode(mode: PinMode?) {
        throw UnsupportedOperationException()
    }

    override fun setProperty(key: String?, value: String?) {
        throw UnsupportedOperationException()
    }

    override fun clearProperties() {
        throw UnsupportedOperationException()
    }

    override fun isExported(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setPullResistance(resistance: PinPullResistance?) {
        throw UnsupportedOperationException()
    }

    override fun removeProperty(key: String?) {
        throw UnsupportedOperationException()
    }

    override fun isMode(mode: PinMode?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getShutdownOptions(): GpioPinShutdown? {
        throw UnsupportedOperationException()
    }

    override fun getProvider(): GpioProvider? {
        throw UnsupportedOperationException()
    }

    override fun setName(name: String?) {
        throw UnsupportedOperationException()
    }

    override fun export(mode: PinMode?) {
        throw UnsupportedOperationException()
    }

    override fun export(mode: PinMode?, defaultState: PinState?) {
        throw UnsupportedOperationException()
    }

    override fun setTag(tag: Any?) {
        throw UnsupportedOperationException()
    }

    override fun removeAllListeners() {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, callback: Callable<Void>?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, blocking: Boolean): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, blocking: Boolean, callback: Callable<Void>?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, pulseState: PinState?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, pulseState: PinState?, callback: Callable<Void>?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, pulseState: PinState?, blocking: Boolean): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun pulse(duration: Long, pulseState: PinState?, blocking: Boolean, callback: Callable<Void>?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun setState(state: PinState?) {
        throw UnsupportedOperationException()
    }

    override fun setState(state: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun low() {
        throw UnsupportedOperationException()
    }

    override fun high() {
        throw UnsupportedOperationException()
    }

    override fun toggle() {
        throw UnsupportedOperationException()
    }

    override fun blink(delay: Long): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun blink(delay: Long, blinkState: PinState?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun blink(delay: Long, duration: Long): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun blink(delay: Long, duration: Long, blinkState: PinState?): Future<*>? {
        throw UnsupportedOperationException()
    }

    override fun isHigh(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isLow(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getState(): PinState? {
        throw UnsupportedOperationException()
    }

    override fun isState(state: PinState?): Boolean {
        throw UnsupportedOperationException()
    }

}