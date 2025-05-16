/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroScript

/**
 * Manages terminal commands for interacting with a MicroPython board.
 *
 * This class provides methods for executing various terminal commands, such as
 * resetting the interpreter, stopping code execution, evaluating code snippets,
 * and executing complete scripts. It utilizes the `BoardManager` to communicate
 * with the MicroPython board.
 */
class TerminalManager(
    /**
     * The `BoardManager` instance used to communicate with the MicroPython board.
     */
    private val boardManager: BoardManager
) {

    /**
     * Terminates the execution of the currently running code on the board.
     *
     * This method sends a termination command (CTRL+C) to the board, effectively
     * stopping any ongoing code execution.
     */
    fun terminateExecution() {
        Log.v(TAG, "terminateExecution")
        boardManager.writeCommand(CommandsManager.TERMINATE)
    }

    /**
     * Resets the MicroPython board or other microcontroller device.
     *
     * This method sends a reset command to the board, causing it to restart.
     * The specific reset command used depends on the type of device.
     *
     * @param microDevice The `MicroDevice` object representing the device to reset.
     * @param onReset An optional callback function that is invoked after the
     *                device has been reset.
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
     * Performs a soft reset of the MicroPython board.
     *
     * This method sends a soft reset command (CTRL+D) to the board, causing
     * it to restart without a full power cycle.
     *
     * @param onReset An optional callback function that is invoked after the
     *                soft reset has been performed.
     */
    fun softResetDevice(onReset: (() -> Unit)? = null) {
        boardManager.writeCommand(CommandsManager.RESET)
        onReset?.invoke()
    }

    /**
     * Evaluates a single line of MicroPython code on the board.
     *
     * This method sends the given code snippet to the board for evaluation.
     * The result of the evaluation is not returned directly but may be printed
     * on the board's console.
     *
     * @param code The MicroPython code to evaluate.
     * @param onEval An optional callback function that is invoked after the
     *               code has been evaluated.
     */
    fun eval(code: String, onEval: (() -> Unit)? = null) {
        Log.i(TAG, "eval - $code")
        boardManager.write(code.trim())
        onEval?.invoke()
    }

    /**
     * Evaluates multi-line MicroPython code on the board.
     *
     * This method sends the given multi-line code to the board for evaluation.
     * It handles line breaks and ensures the code is properly executed.
     *
     * @param code The multi-line MicroPython code to evaluate.
     * @param onEval An optional callback function that is invoked after the
     *               code has been evaluated.
     */
    fun evalMultiLine(code: String, onEval: (() -> Unit)? = null) {
        boardManager.write(code.replace("\n", "\r").trim())
        boardManager.write("\r")
        onEval?.invoke()
    }

    /**
     * Executes a complete MicroPython script on the board.
     *
     * This method sends the given script to the board and executes it in silent
     * mode. The output of the script is not returned directly but may be printed
     * on the board's console.
     *
     * @param code The MicroPython script to execute.
     */
    suspend fun executeLocalScript(
        microScript: MicroScript,
        onClear: () -> Unit
    ) {
        // reset the device to clear previously imported modules
        boardManager.writeCommand(CommandsManager.RESET)
        delay(100)
        onClear()
        // Start silent mode.
        boardManager.writeCommand(CommandsManager.SILENT_MODE)
        // Print a new line to separate the silent mode message from the output.
        // And write the code to interpreter to execute it
        boardManager.writeCommand("print()\r\n${microScript.content}")
        // Exit silent mode.
        boardManager.writeCommand(CommandsManager.RESET)
        // Back to REPL mode.
        boardManager.writeCommand(CommandsManager.REPL_MODE)
    }

    fun executeScript(microScript: MicroScript) {
        // reset the device to clear previously imported modules
        boardManager.writeCommand(CommandsManager.RESET)
        // Start silent mode.
        //boardManager.writeCommand(CommandsManager.SILENT_MODE)
        // Locate the working directory to the script
        boardManager.write(CommandsManager.chDir(microScript.scriptDir))
        // Run the script
        boardManager.write("import ${microScript.nameWithoutExt}")
        // Exit silent mode.
        boardManager.writeCommand(CommandsManager.RESET)
        // Back to REPL mode.
        boardManager.writeCommand(CommandsManager.REPL_MODE)
    }


    companion object {
        private const val TAG = "TerminalManager"
    }
}