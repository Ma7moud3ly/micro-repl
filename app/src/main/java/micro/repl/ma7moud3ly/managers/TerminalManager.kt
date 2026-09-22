/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import kotlinx.coroutines.delay
import org.koin.core.annotation.Single
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroScript
import kotlin.time.Duration.Companion.milliseconds

/**
 * Manages terminal commands for interacting with a MicroPython board.
 *
 * This class provides methods for executing various terminal commands, such as
 * resetting the interpreter, stopping code execution, evaluating code snippets,
 * and executing complete scripts. It utilizes the `ReplManager` to communicate
 * with the MicroPython board.
 */
@Single
class TerminalManager(
    /**
     * The `ReplManager` instance used to communicate with the MicroPython board.
     */
    private val replManager: ReplManager
) {

    /**
     * Terminates the execution of the currently running code on the board.
     *
     * This method sends a termination command (CTRL+C) to the board, effectively
     * stopping any ongoing code execution.
     */
    suspend fun terminateExecution() {
        AppLog.v(TAG, "terminateExecution")
        replManager.writeCommand(CommandsManager.TERMINATE)
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
    suspend fun resetDevice(
        microDevice: MicroDevice,
        onReset: (() -> Unit)? = null
    ) {
        val cmd = if (microDevice.isMicroPython) "machine.reset()" else ""
        replManager.write(cmd)
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
    suspend fun softResetDevice(onReset: (() -> Unit)? = null) {
        replManager.writeCommand(CommandsManager.RESET)
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
    suspend fun eval(code: String, onEval: (() -> Unit)? = null) {
        AppLog.i(TAG, "eval - $code")
        replManager.write(code.trim())
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
    suspend fun evalMultiLine(code: String, onEval: (() -> Unit)? = null) {
        replManager.write(code.replace("\n", "\r").trim())
        replManager.write("\r")
        onEval?.invoke()
    }

    /**
     * Executes a complete MicroPython script on the board.
     *
     * This method sends the given script to the board and executes it in silent
     * mode. The output of the script is not returned directly but may be printed
     * on the board's console.
     *
     * @param microScript The MicroPython script to execute.
     */
    suspend fun executeLocalScript(
        microScript: MicroScript,
        onClear: () -> Unit
    ) {
        // stop any running code/loops
        replManager.writeCommand(CommandsManager.TERMINATE)
        // Start silent mode.
        replManager.writeCommand(CommandsManager.SILENT_MODE)
        // reset the device to clear previously imported modules
        replManager.writeCommand(CommandsManager.RESET)
        // clear noise from the terminal outputs
        delay(100.milliseconds)
        onClear()
        // Print a new line to separate the silent mode message from the output.
        // And write the code to interpreter to execute it
        replManager.writeCommand("print()\r\n${microScript.content}")
        // Exit silent mode.
        replManager.writeCommand(CommandsManager.RESET)
        // Back to REPL mode.
        replManager.writeCommand(CommandsManager.REPL_MODE)
    }

    suspend fun executeScript(microScript: MicroScript, onClear: () -> Unit) {
        // Start silent mode.
        replManager.writeCommand(CommandsManager.SILENT_MODE)
        // reset the device to clear previously imported modules
        replManager.writeCommand(CommandsManager.RESET)
        // clear noise from the terminal outputs
        delay(100.milliseconds)
        onClear()
        // Locate the working directory to the script
        replManager.write(CommandsManager.chDir(microScript.scriptDir))
        // Run the script
        replManager.write("import ${microScript.nameWithoutExt}")
        // Exit silent mode.
        replManager.writeCommand(CommandsManager.RESET)
        // Back to REPL mode.
        replManager.writeCommand(CommandsManager.REPL_MODE)
    }


    companion object {
        private const val TAG = "TerminalManager"
    }
}