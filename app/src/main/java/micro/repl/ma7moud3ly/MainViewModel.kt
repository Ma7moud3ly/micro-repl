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
    private var editorMode = EditorMode.LOCAL
    var scriptPath = mutableStateOf("")
        private set
    var scriptContent = ""
        private set

    val isLocalScript: Boolean get() = editorMode == EditorMode.LOCAL

    //for terminal
    val terminalInput = mutableStateOf("")
    val terminalOutput = mutableStateOf("")
    val history = TerminalHistory()

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

}
