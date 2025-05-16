package micro.repl.ma7moud3ly.model

import androidx.compose.runtime.mutableStateOf

/**
 * Represents the state of the code editor.
 *
 * This data class holds information about the current script being edited,
 * including its content, path, title, and various editor settings. It also
 * provides properties for tracking the availability of undo/redo actions
 * and the run button.
 *
 * @param microScript The initial `MicroScript` object to load into the editor.
 * @param isBlank Indicates whether the editor is initially blank (no script loaded).
 */
data class EditorState(
    private var microScript: MicroScript,
    val isBlank: Boolean = false
) {
    /**
     * Indicates whether the current script is stored locally or at board.
     */
    val isLocal: Boolean get() = microScript.isLocal

    /**
     * Indicates whether the current script is a MicroPython script.
     */
    val microPython: Boolean get() = microScript.microPython

    /**
     * Indicates whether the current script has an associated file path (i.e., it exists).
     */
    val exists: Boolean get() = microScript.exists || path.isEmpty().not()

    /**
     * The mutable state of the editor's title.
     */
    val title = mutableStateOf(microScript.path)

    /**
     * The file path of the current script.
     */
    var path: String = microScript.path

    /**
     * The content of the current script.
     */
    var content: String = microScript.content

    /**
     * Indicates whether the current script is a Python script.
     * It could be python but not MicroPython :3
     */
    var isPython = microScript.isPython

    /**
     * The mutable state of the undo availability.
     */
    val canUndo = mutableStateOf(false)

    /**
     * The mutable state of the redo availability.
     */
    val canRedo = mutableStateOf(false)

    /**
     * The mutable state of the run button availability.
     */
    val canRun = mutableStateOf(false)

    /**
     * The mutable state of the line numbers visibility.
     */
    val showLines = mutableStateOf(false)

    /**
     * Converts the `EditorState` object to a `MicroScript` object.
     */
    val asMicroScript: MicroScript
        get() = MicroScript(
            content = content,
            path = path,
            editorMode = microScript.editorMode
        )

    /**
     * Resets the editor state with a new title, clearing the path and content.
     *
     * @param newTitle The new title for the editor.
     */
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


