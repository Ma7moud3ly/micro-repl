/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalHistoryManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.model.MicroScript

/**
 * Holds and manages the UI state for the main application screen.
 *
 * This ViewModel class provides data and state management for the main screen
 * of the application. It exposes LiveData objects for observing changes in
 * the device connection status, connected device, files explorer path, files
 * list, terminal input and output, and command history.
 */
class MainViewModel : ViewModel() {

    ////// Board session

    /**
     * Talks to the connected board over USB serial.
     *
     * Null until [attach] runs, and again once the hosting Activity is destroyed.
     */
    var boardManager by mutableStateOf<BoardManager?>(null)
        private set

    /** Runs code and resets the board. Shares the [boardManager] connection. */
    var terminalManager by mutableStateOf<TerminalManager?>(null)
        private set

    /** Reads and writes files on the board. Shares the [boardManager] connection. */
    var filesManager by mutableStateOf<FilesManager?>(null)
        private set

    /**
     * Builds the board session on top of [context].
     *
     * [BoardManager] still needs an Activity for the USB permission intent, so the
     * session is scoped to whoever calls this and must be torn down with [detach].
     * The state below (connection status, terminal output, files) is what survives
     * a configuration change.
     */
    fun attach(context: Context) {
        if (boardManager != null) return
        val board = BoardManager(
            context = context,
            onStatusChanges = { status.value = it },
            onReceiveData = ::onReceiveTerminalData
        )
        boardManager = board
        terminalManager = TerminalManager(board)
        filesManager = FilesManager(
            boardManager = board,
            onUpdateFiles = { files.value = it }
        )
        board.start()
    }

    /** Tears down the session built by [attach]. */
    fun detach() {
        boardManager?.release()
        boardManager = null
        terminalManager = null
        filesManager = null
    }

    /**
     * Appends board output to the terminal on the main thread, since it arrives on the
     * serial reader thread.
     */
    private fun onReceiveTerminalData(data: String, clear: Boolean) {
        viewModelScope.launch {
            terminalOutput.value = when {
                clear -> ""
                // limit terminal output to 10000 chars to avoid app
                // freeze for very large outputs
                terminalOutput.value.length > 10000 -> data
                else -> terminalOutput.value + data
            }
        }
    }

    ////// Home

    /**
     * Represents the current connectivity status of the device.
     *
     * Possible values are:
     * - `ConnectionStatus.Connecting`: Indicates that the device is currently
     *   attempting to connect.
     * - `ConnectionStatus.Connected`: Indicates that the device is successfully
     *   connected.
     * - `ConnectionStatus.Error`: Indicates that the connection attempt
     *   failed.
     */
    val status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connecting)

    /**
     * The currently connected MicroPython device.
     *
     * This property is only available when the `status` is `ConnectionStatus.Connected`.
     * Otherwise, it returns `null`.
     */
    val microDevice: MicroDevice? get() = (status.value as? ConnectionStatus.Connected)?.microDevice

    ////// Script handoff

    var script by mutableStateOf(MicroScript())
        private set

    /** Sets the script for the screen being opened. Pass `MicroScript()` for a blank one. */
    fun openScript(script: MicroScript) {
        this.script = script
    }


    ////// Files Explorer

    /**
     * The current path being displayed in the files explorer.
     */
    val root = mutableStateOf("/")

    /**
     * The list of files and directories in the current path of the files explorer.
     */
    val files = MutableStateFlow<List<MicroFile>>(listOf())

    ////// Terminal

    /**
     * The current input text in the terminal.
     */
    val terminalInput = mutableStateOf("")

    /**
     * The current output text in the terminal.
     */
    val terminalOutput = mutableStateOf("")

    /**
     * Manages the command history for the terminal.
     */
    val history = TerminalHistoryManager()
}