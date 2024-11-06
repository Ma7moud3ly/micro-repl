package micro.repl.ma7moud3ly.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
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
    val asJson: String get() = Json.encodeToString(this)
}

fun String.asMicroScript(): MicroScript = try {
    if (this.isEmpty()) MicroScript()
    else Json.decodeFromString<MicroScript>(this)
} catch (e: Exception) {
    MicroScript()
}