package com.ma7moud3ly.microterminal.managers

import com.ma7moud3ly.microterminal.utils.MicroDevice

class TerminalManager(
    private val deviceManager: DeviceManager
) {

    fun terminateExecution(onTerminate: (() -> Unit)? = null) {
        val cmd = "\u0003"
        deviceManager.write(cmd)
        onTerminate?.invoke()
    }

    fun resetDevice(
        microDevice: MicroDevice,
        onReset: (() -> Unit)? = null
    ) {
        val cmd = if (microDevice.isMicroPython) "machine.reset()" else ""
        deviceManager.write(cmd)
        onReset?.invoke()
    }

    fun softResetDevice(
        microDevice: MicroDevice,
        onReset: (() -> Unit)? = null
    ) {
        val cmd = if (microDevice.isMicroPython) "machine.soft_reset()" else ""
        deviceManager.write(cmd)
        onReset?.invoke()
    }

    fun run(code: String, onRun: (() -> Unit)? = null) {
        deviceManager.write(code)
        onRun?.invoke()
    }

    fun execute(
        code: String,
        terminateFirst: Boolean = false,
        onExecute: (() -> Unit)? = null
    ) {
        val cmd = CommandsManager.execute(code, toJson = true)
        if (terminateFirst) deviceManager.write("\u0003")
        deviceManager.write(cmd)
        onExecute?.invoke()
    }
}