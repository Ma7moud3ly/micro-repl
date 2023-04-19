/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log
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
        Log.i(TAG, "eval - $code")
        boardManager.write(code.replace("\n", "\r\n").trimEnd())
        onEval?.invoke()
    }

    fun executeScript(
        code: String,
        onExecute: (() -> Unit)? = null
    ) {
        //start silent mode
        boardManager.writeCommand(CommandsManager.SILENT_MODE)
        //print new line to separate silent mode message from output
        boardManager.writeCommand("print()\r\n$code")
        //exit silent mode
        boardManager.writeCommand(CommandsManager.RESET)
        //back to repl mode
        boardManager.writeCommand(CommandsManager.REPL_MODE)
        onExecute?.invoke()
    }

    companion object {
        private const val TAG = "TerminalManager"
    }
}