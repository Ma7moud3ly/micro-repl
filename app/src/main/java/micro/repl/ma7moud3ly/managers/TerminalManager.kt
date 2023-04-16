/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import micro.repl.ma7moud3ly.utils.MicroDevice

/**
 * This class manages the terminal commands
 * such reset interpreter / stop execution / eval code / execute script
 */
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
        boardManager.writeCommand(CommandsManager.RESET)
        onReset?.invoke()
    }

    fun eval(code: String, onEval: (() -> Unit)? = null) {
        boardManager.write(code)
        onEval?.invoke()
    }

    fun executeScript(
        code: String,
        onExecute: (() -> Unit)? = null
    ) {
        val cmd = CommandsManager.execute(code, toJson = true)
        boardManager.writeCommand(CommandsManager.SILENT_MODE)
        boardManager.writeCommand(cmd)
        boardManager.writeCommand(CommandsManager.RESET) //exit silent mode
        boardManager.writeCommand(CommandsManager.REPL_MODE)
        onExecute?.invoke()
    }
}