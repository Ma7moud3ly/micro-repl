package micro.repl.ma7moud3ly.model

import androidx.compose.runtime.mutableStateOf
import java.io.File

data class MicroScript(
    var path: String = "",
    var content: String = "",
    val editorMode: EditorMode = EditorMode.LOCAL,
    val microPython: Boolean = true
) {
    val exists: Boolean get() = path.isEmpty().not()
    val hasContent: Boolean get() = exists && content.isEmpty().not()
    val file: File get() = File(path)
    val name: String get() = file.name
    val isPython: Boolean get() = name.trim().endsWith(".py")
    val isLocal: Boolean get() = editorMode == EditorMode.LOCAL
    val title = mutableStateOf(name)
    val canUndo = mutableStateOf(false)
    val canRedo = mutableStateOf(false)
    val canRun = mutableStateOf(false)
    val isDark = mutableStateOf(false)
    val showLines = mutableStateOf(false)
    val showTitle = mutableStateOf(false)
}

enum class EditorMode {
    LOCAL,
    REMOTE
}


