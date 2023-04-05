package com.ma7moud3ly.microterminal.managers

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

    fun execute(code: String, onExecute: (() -> Unit)? = null) {
        deviceManager.write(code)
        onExecute?.invoke()
    }

    fun eval(code: String, onEval: (() -> Unit)? = null) {
        val cmd = "content = '''$code''';eval(content)"
        deviceManager.write(cmd)
        onEval?.invoke()
    }
}