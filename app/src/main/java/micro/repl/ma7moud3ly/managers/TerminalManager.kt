package micro.repl.ma7moud3ly.managers

import micro.repl.ma7moud3ly.utils.MicroDevice

class TerminalManager(
    private val boardManager: BoardManager
) {

    fun terminateExecution(onTerminate: (() -> Unit)? = null) {
        boardManager.writeCommand(CommandsManager.TERMINATE)
        onTerminate?.invoke()
    }

    fun resetDevice(
        microDevice: MicroDevice,
        onReset: (() -> Unit)? = null
    ) {
        val cmd = if (microDevice.isMicroPython) "machine.reset()" else ""
        boardManager.write(cmd)
        onReset?.invoke()
    }

    fun softResetDevice(onReset: (() -> Unit)? = null) {
        val cmd = CommandsManager.SOFT_RESET
        boardManager.writeCommand(cmd)
        onReset?.invoke()
    }

    fun eval(code: String, onEval: (() -> Unit)? = null) {
        boardManager.write(code)
        onEval?.invoke()
    }

    fun executeScript(
        code: String,
        terminateFirst: Boolean = false,
        onExecute: (() -> Unit)? = null
    ) {
        val cmd = CommandsManager.execute(code, toJson = true)
        boardManager.writeCommand(CommandsManager.HIDDEN_MODE)
        boardManager.writeCommand(cmd)
        boardManager.writeCommand(CommandsManager.SOFT_RESET)
        boardManager.writeCommand(CommandsManager.REPL_MODE)
        onExecute?.invoke()
    }
}