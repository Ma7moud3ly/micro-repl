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
 * such as reset interpreter, stop execution, evaluate code, and execute scripts.
 */
class TerminalManager(
    /**
     * The board manager used to communicate with microcontroller.
     */
    private val boardManager: BoardManager
) {

    /**
     * Terminates the execution of the code on the board.
     *
     * @param onTerminate Optional callback to be invoked after the execution has been terminated.
     */
    fun terminateExecution(onTerminate: (() -> Unit)? = null) {
        boardManager.writeCommand(CommandsManager.TERMINATE)
        onTerminate?.invoke()
    }

    /**
     * Resets the device.
     *
     * @param microDevice The micro device to reset.
     * @param onReset Optional callback to be invoked after the device has been reset.
     */
    fun resetDevice(
        microDevice: MicroDevice,
        onReset: (() -> Unit)? = null
    ) {
        val cmd = if (microDevice.isMicroPython) "machine.reset()" else ""
        boardManager.write(cmd)
        onReset?.invoke()
    }

    /**
     * Performs a soft reset of the device.
     *
     * @param onReset Optional callback to be invoked after the soft reset has been performed.
     */
    fun softResetDevice(onReset: (() -> Unit)? = null) {
        boardManager.writeCommand(CommandsManager.RESET)
        onReset?.invoke()
    }

    /**
     * Evaluates the given code on the board.
     *
     * @param code The code to evaluate.
     * @param onEval Optional callback to be invoked after the code has been evaluated.
     */
    fun eval(code: String, onEval: (() -> Unit)? = null) {
        Log.i(TAG, "eval - $code")
        boardManager.write(code.replace("\n", "\r\n").trimEnd())
        boardManager.write("\r\n")
        onEval?.invoke()
    }

    /**
     * Executes the given script on the board.
     *
     * @param code The script to execute.
     * @param onExecute Optional callback to be invoked after the script has been executed.
     */
    fun executeScript(
        code: String,
        onExecute: (() -> Unit)? = null
    ) {
        // Start silent mode.
        boardManager.writeCommand(CommandsManager.SILENT_MODE)

        // Print a new line to separate the silent mode message from the output.
        boardManager.writeCommand("print()\r\n$code")

        // Exit silent mode.
        boardManager.writeCommand(CommandsManager.RESET)

        // Back to REPL mode.
        boardManager.writeCommand(CommandsManager.REPL_MODE)

        onExecute?.invoke()
    }

    companion object {
        private const val TAG = "TerminalManager"
    }
}