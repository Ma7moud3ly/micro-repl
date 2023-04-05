package com.ma7moud3ly.microterminal

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ma7moud3ly.microterminal.managers.ConnectionStatus
import com.ma7moud3ly.microterminal.managers.EditorMode
import com.ma7moud3ly.microterminal.managers.MicroDevice
import com.ma7moud3ly.microterminal.managers.MicroFile
import com.ma7moud3ly.microterminal.utils.TerminalHistory
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    val status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.OnConnecting)

    val root = mutableStateOf("")
    val files = MutableStateFlow<List<MicroFile>>(listOf())
    val isConnected: Boolean get() = status.value is ConnectionStatus.OnConnected
    val microDevice: MicroDevice? get() = (status.value as? ConnectionStatus.OnConnected)?.microDevice

    //for editor
    var editorMode = EditorMode.LOCAL
    var scriptPath = mutableStateOf("")
    var script = ""

    //for terminal
    val terminalInput = mutableStateOf("")
    val terminalOutput = mutableStateOf("")
    var newExecution = false
    val history = TerminalHistory()
}
