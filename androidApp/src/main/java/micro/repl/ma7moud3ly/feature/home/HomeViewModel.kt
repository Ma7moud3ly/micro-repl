/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.ScriptManager
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import org.koin.core.annotation.KoinViewModel


@KoinViewModel
class HomeViewModel(
    private val boardManager: BoardManager,
    private val terminalManager: TerminalManager,
    private val scriptManager: ScriptManager
) : ViewModel() {

    /** Current connectivity of the board. */
    val status: StateFlow<ConnectionStatus> = boardManager.status

    private val _commands = Channel<HomeCommand>(Channel.BUFFERED)

    /** Feedback for the screen to show. Buffered, so nothing is missed. */
    val commands: Flow<HomeCommand> = _commands.receiveAsFlow()

    private val microDevice: MicroDevice?
        get() = (status.value as? ConnectionStatus.Connected)?.microDevice

    /** Clears the handoff, so the terminal or editor opens on nothing. */
    fun newScript() {
        scriptManager.open(MicroScript())
    }

    ////// Connection

    fun connect() {
        viewModelScope.launch { boardManager.detectUsbDevices() }
    }

    fun disconnect() {
        boardManager.onDisconnectDevice()
    }

    fun approveDevice(microDevice: MicroDevice) {
        viewModelScope.launch { boardManager.approveDevice(microDevice) }
    }

    fun denyDevice() {
        boardManager.onDenyDevice()
    }

    fun forgetDevice(microDevice: MicroDevice) {
        viewModelScope.launch { boardManager.onForgetDevice(microDevice) }
    }

    ////// Board control

    /** Hard reset. Only a connected board can be reset. */
    fun reset() {
        val device = microDevice ?: return
        viewModelScope.launch {
            terminalManager.resetDevice(device) {
                _commands.trySend(HomeCommand.DeviceReset)
            }
        }
    }

    fun softReset() {
        if (microDevice == null) return
        viewModelScope.launch {
            terminalManager.softResetDevice {
                _commands.trySend(HomeCommand.DeviceSoftReset)
            }
        }
    }

    fun terminate() {
        viewModelScope.launch {
            terminalManager.terminateExecution()
            _commands.trySend(HomeCommand.ExecutionTerminated)
        }
    }
}
