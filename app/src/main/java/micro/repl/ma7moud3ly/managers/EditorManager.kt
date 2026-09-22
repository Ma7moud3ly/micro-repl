/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import micro.repl.ma7moud3ly.managers.port.ScriptsManager
import micro.repl.ma7moud3ly.managers.port.StorageManager
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.File


/**
 * Manages the state and actions of the code editor.
 *
 * This class handles editor settings, code execution, file operations (save, undo/redo),
 * and coordinates between the UI and the underlying [EditorSession].
 *
 * @param session The current editor session containing code and script metadata.
 * @param scriptsManager Manager for scripts stored on this device.
 * @param storageManager Persists the editor settings between launches.
 * @param filesManager Manager for remote file operations on a MicroPython board.
 * @param theme The theme the editor opens with.
 * @param canRunState Whether the board is connected, as observable state so the
 *                    toolbar follows a disconnect while the editor is open.
 */
class EditorManager(
    private val session: EditorSession,
    private val scriptsManager: ScriptsManager,
    private val storageManager: StorageManager,
    private val filesManager: FilesManager,
    theme: EditorTheme,
    private val canRunState: State<Boolean>
) {

    /** The configuration for the editor, restored from [storageManager]. */
    val settings = EditorSettings(
        theme = theme,
        fontSize = storageManager.fontSize,
        showLineNumbers = storageManager.showLineNumbers
    )


    val codeState: CodeState get() = session.codeState
    val script: MicroScript get() = session.script
    val isLocal: Boolean get() = script.isLocal
    val isMicroPython: Boolean get() = script.microPython
    val isPython: Boolean get() = script.isPython
    val scriptName: String get() = script.name

    /** The editor title (file name / path), shown in the header. */
    val title: State<String> = derivedStateOf { script.displayName }

    /**
     * Whether the run button is available.
     *
     * Reading it in composition subscribes to [canRunState], so the toolbar goes
     * back to read-only the moment the board disconnects.
     */
    val canRun: Boolean get() = canRunState.value

    /** Whether there are edits that haven't been written back yet. */
    val isDirty: Boolean get() = session.isDirty

    /**
     * Whether the undo is available.
     */
    val canUndo: Boolean by derivedStateOf {
        codeState.code
        codeState.canUndo()
    }

    /**
     * Whether the redo is available.
     */
    val canRedo: Boolean by derivedStateOf {
        codeState.code
        codeState.canRedo()
    }

    /** Line-numbers visibility, owned by [settings]. */
    val showLines: Boolean get() = settings.showLineNumbersState.value

    /** The live buffer as a script, for handing to the terminal. */
    val asMicroScript: MicroScript get() = session.asMicroScript

    /**
     * Editor actions
     */

    fun clear() {
        codeState.updateText("")
    }

    fun undo() {
        codeState.undo()
    }

    fun redo() {
        codeState.redo()
    }

    fun toggleLines() {
        settings.toggleLinesNumber()
    }

    fun zoomIn() {
        settings.zoomIn()
    }

    fun zoomOut() {
        settings.zoomOut()
    }

    /** Persists the editor settings. Called when the editor is disposed. */
    fun release() {
        persistSettings()
    }

    /** Empties the buffer for a new, unnamed script. */
    fun reset() {
        session.reset()
    }

    /** True if the script exists and has unsaved changes. */
    fun saveExisting(): Boolean = session.isDirty

    /** True if the script is new (no path) and has content. */
    fun saveNew(): Boolean = session.isNew

    /**
     * Saves the current script locally or to the MicroPython board.
     *
     * Both paths suspend: the managers move the actual IO off the caller's thread.
     */
    suspend fun save() {
        if (script.isLocal) {
            val file = File(script.path)
            val content = codeState.code
            val saved = scriptsManager.write(file, content)
            if (saved) session.markSaved()
        } else {
            filesManager.write(path = script.path, content = codeState.code)
            session.markSaved()
        }
    }

    /**
     * Saves the current script under a new file name.
     */
    suspend fun saveFileAs(name: String) {
        scriptsManager.scriptDirectory()?.let {
            session.moveTo(it.path + "/" + name)
            AppLog.v(TAG, "saveFileAs - ${script.path}")
            save()
        }
    }

    /** Cheap enough to stay synchronous: the platform write itself is async. */
    fun persistSettings() {
        storageManager.showLineNumbers = settings.showLineNumbersState.value
        storageManager.fontSize = settings.fontSizeState.value
        if (script.isLocal && script.exists) {
            AppLog.v(TAG, "persistSettings - hasScript")
            storageManager.recentScript = script.path
        }
    }

    companion object {
        private const val TAG = "EditorManager"
    }
}
