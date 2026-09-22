/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import micro.repl.ma7moud3ly.managers.port.SerialPortManager
import micro.repl.ma7moud3ly.managers.CommandsManager.isSilentExecutionDone
import micro.repl.ma7moud3ly.managers.CommandsManager.trimSilentResult
import micro.repl.ma7moud3ly.model.BoardOutput
import micro.repl.ma7moud3ly.model.ExecutionMode
import org.koin.core.annotation.Single

/**
 * Speaks the MicroPython REPL protocol over a [SerialPortManager].
 *
 * Holds the framing rules, the interactive/silent execution modes and the response
 * parsing.
 */
@Single
class ReplManager(
    private val serialPort: SerialPortManager
) {

    companion object {
        private const val TAG = "ReplManager"
    }

    private val executionMode = MutableStateFlow(ExecutionMode.INTERACTIVE)

    /**
     * REPL output while in interactive mode.
     *
     * Cold on purpose - nothing is buffered while no screen is listening, and silent
     * execution is filtered out because its result is returned by [writeInSilentMode].
     */
    val output: Flow<BoardOutput> = serialPort.incoming
        .filter { executionMode.value == ExecutionMode.INTERACTIVE }
        .map { removeEnding(it.decodeToString()) }
        .onEach { if (isDebug) AppLog.v(TAG, "onNewData - ${Json.encodeToString(it)}") }
        .filter { it.isNotEmpty() && it.trim() != ">>>" }
        .map { BoardOutput(data = it, clear = it.contains(CommandsManager.CLEAR)) }

    /**
     * Writes the given code to the serial REPL.
     *
     *  - CR is required before code to print >>>
     *  - and again after code to echo the response
     */
    suspend fun write(code: String) {
        AppLog.v(TAG, "write: $code")
        val cmd = "\u000D" + code + "\u000D"
        serialPort.write(cmd.encodeToByteArray())
    }

    /**
     * Writes a REPL command (control characters and the like) to the serial port.
     */
    suspend fun writeCommand(code: String) {
        if (isDebug) AppLog.i(TAG, "writeCommand - ${Json.encodeToString(code)}")
        serialPort.write(code.encodeToByteArray())
    }

    /**
     * Runs [code] in silent mode and returns its output.
     *
     * In silent mode the board output is collected here instead of being echoed to
     * [output], so callers get the result of exactly the code they sent.
     */
    suspend fun writeInSilentMode(code: String): String = withContext(Dispatchers.IO) {
        AppLog.i(TAG, "writeInSilentMode - $code")
        executionMode.value = ExecutionMode.SCRIPT
        val syncData = StringBuilder("")
        try {
            serialPort.incoming
                // onSubscription, not onStart: the writes below must not run until
                // this collector is registered, or a fast board answers into the void.
                .onSubscription {
                    writeCommand(CommandsManager.SILENT_MODE)
                    write(code)
                    writeCommand(CommandsManager.RESET)
                }
                .onEach { syncData.append(it.decodeToString()) }
                .first { isSilentExecutionDone(syncData.toString()) }

            val result = trimSilentResult(syncData.toString())
            AppLog.v(TAG, "syncResult - $result")
            return@withContext result
        } finally {
            executionMode.value = ExecutionMode.INTERACTIVE
        }
    }

    private fun removeEnding(input: String): String {
        val regexPattern = Regex("\\n>>>\\s*(?:\\r\\n>>>\\s*)*$")
        return regexPattern.replace(input, "")
    }
}



