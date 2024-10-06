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
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.managers.TerminalHistoryManager
import micro.repl.ma7moud3ly.model.toMicroDevice

class MainViewModel : ViewModel() {
    //the device connectivity status Connected,Connecting or OnFailure
    val status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connecting)

    //the connected device
    val microDevice: MicroDevice? get() = (status.value as? ConnectionStatus.Connected)?.microDevice

    // the current path in files explorer
    val root = mutableStateOf("")

    // files list in files explorer
    val files = MutableStateFlow<List<MicroFile>>(listOf())

    /**
     * for editor
     */

    //is script saved locally or in micropython device
    private var editorMode = EditorMode.LOCAL
    val isLocalScript: Boolean get() = editorMode == EditorMode.LOCAL

    //the script path
    var scriptPath = mutableStateOf("")
        private set

    //python code in script
    var scriptContent = ""
        private set

    fun initScript(
        path: String,
        source: EditorMode,
        content: String = "",
    ) {
        this.editorMode = source
        this.scriptPath.value = path
        this.scriptContent = content
    }

    fun initScript(
        path: String,
        content: String
    ) {
        this.scriptPath.value = path
        this.scriptContent = content
    }


    /**
     * for terminal
     */

    val terminalInput = mutableStateOf("")
    val terminalOutput = mutableStateOf("")

    //commands history
    val history = TerminalHistoryManager()
}
