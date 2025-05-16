/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import micro.repl.ma7moud3ly.managers.TerminalHistoryManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroFile

/**
 * Holds and manages the UI state for the main application screen.
 *
 * This ViewModel class provides data and state management for the main screen
 * of the application. It exposes LiveData objects for observing changes in
 * the device connection status, connected device, files explorer path, files
 * list, terminal input and output, and command history.
 */
class MainViewModel : ViewModel() {

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