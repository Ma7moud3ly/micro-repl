package micro.repl.ma7moud3ly

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import micro.repl.ma7moud3ly.utils.*

class MainViewModel : ViewModel() {
    val status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.OnConnecting)

    val root = mutableStateOf("")
    val files = MutableStateFlow<List<MicroFile>>(listOf())
    val isConnected: Boolean get() = status.value is ConnectionStatus.OnConnected
    val microDevice: MicroDevice? get() = (status.value as? ConnectionStatus.OnConnected)?.microDevice

    //for editor
    var editorMode = EditorMode.LOCAL
    var scriptPath = mutableStateOf("")

    //for terminal
    val terminalInput = mutableStateOf("")
    val terminalOutput = mutableStateOf("")
    val history = TerminalHistory()
    var script = ""

}
