/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.ReplManager
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalHistoryManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.KoinViewModel

/**
 * Holds and manages the UI state for the main application screen.
 *
 * The managers are process-wide singletons; this class owns the screen state built on
 * top of them and the scope their suspending work runs in.
 */
@KoinViewModel
class MainViewModel(
    val boardManager: BoardManager,
    val replManager: ReplManager,
    val terminalManager: TerminalManager,
    val filesManager: FilesManager
) : ViewModel() {

    ////// Home

    /**
     * Represents the current connectivity status of the device.
     */
    val status: StateFlow<ConnectionStatus> get() = boardManager.status

    /**
     * The currently connected MicroPython device.
     */
    val microDevice: MicroDevice? get() = (status.value as? ConnectionStatus.Connected)?.microDevice

    /** Starts the board session and keeps it running for this ViewModel's lifetime. */
    fun start() {
        viewModelScope.launch { boardManager.start() }
    }

    fun detectUsbDevices() {
        viewModelScope.launch { boardManager.detectUsbDevices() }
    }

    fun approveDevice(microDevice: MicroDevice) {
        viewModelScope.launch { boardManager.approveDevice(microDevice) }
    }

    fun onForgetDevice(microDevice: MicroDevice) {
        viewModelScope.launch { boardManager.onForgetDevice(microDevice) }
    }

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
    val files: StateFlow<List<MicroFile>> get() = filesManager.files

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

    init {
        viewModelScope.launch {
            replManager.output.collect { (data, clear) ->
                terminalOutput.value = when {
                    clear -> ""
                    // limit terminal output to 10000 chars to avoid app
                    // freeze for very large outputs
                    terminalOutput.value.length > 10000 -> data
                    else -> terminalOutput.value + data
                }
            }
        }
    }
}
