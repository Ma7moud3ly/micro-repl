package micro.repl.ma7moud3ly.model

data class MicroScript(
    var path: String = "",
    var content: String = "",
    val editorMode: EditorMode = EditorMode.LOCAL,
    val microPython: Boolean = true
) {
    val exists: Boolean get() = path.isEmpty().not()
    val hasContent: Boolean get() = exists && content.isEmpty().not()
    val scriptDir: String get() = path.substringBeforeLast('/', "")
    val nameWithoutExt: String get() = name.replace(".py", "")
    val name: String get() = path.substringAfterLast('/')
    val displayName: String
        get() = when {
            exists.not() -> "/untitled"
            isLocal -> "/$name"
            else -> path
        }
    val isPython: Boolean get() = name.trim().endsWith(".py")
    val isLocal: Boolean get() = editorMode == EditorMode.LOCAL
}

