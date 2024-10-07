package micro.repl.ma7moud3ly.model

import androidx.compose.runtime.mutableStateOf

data class EditorState(private var microScript: MicroScript) {
    val isLocal: Boolean get() = microScript.isLocal
    val microPython: Boolean get() = microScript.microPython
    val exists: Boolean get() = microScript.exists || path.isEmpty().not()
    val title = mutableStateOf(microScript.name)
    var path: String = microScript.path
    var content: String = microScript.content
    var isPython = microScript.isPython
    val canUndo = mutableStateOf(false)
    val canRedo = mutableStateOf(false)
    val canRun = mutableStateOf(false)
    val isDark = mutableStateOf(false)
    val showLines = mutableStateOf(false)

    val asMicroScript: MicroScript
        get() = MicroScript(
            content = content,
            path = path,
            editorMode = microScript.editorMode
        )

    fun reset(newTitle: String) {
        title.value = newTitle
        path = ""
        content = ""
    }
}

enum class EditorMode {
    LOCAL,
    REMOTE
}


