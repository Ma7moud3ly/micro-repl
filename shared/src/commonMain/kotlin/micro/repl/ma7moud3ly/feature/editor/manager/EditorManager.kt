/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.editor.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.RemoteFilesManager
import micro.repl.ma7moud3ly.managers.ScriptManager
import micro.repl.ma7moud3ly.managers.ThemesManager
import micro.repl.ma7moud3ly.platform.LocalFilesManager
import micro.repl.ma7moud3ly.managers.StorageManager
import micro.repl.ma7moud3ly.feature.editor.model.EditorAction
import micro.repl.ma7moud3ly.feature.editor.model.EditorCommand
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.platform.AppLog
import org.koin.core.annotation.Factory


/**
 * Manages the state and actions of the code editor.
 *
 * This class handles editor settings, code execution, file operations (save, undo/redo),
 * and coordinates between the UI and the underlying [EditorSession].
 *
 *
 * @param localFilesManager Manager for scripts stored on this device.
 * @param remoteFilesManager Manager for remote file operations on a MicroPython board.
 * @param scriptManager The script the editor was opened on.
 * @param storageManager Persists the editor settings between launches.
 * @param themesManager The theme the editor opens with.
 * @param boardManager Tells the toolbar whether a script can be run right now.
 */
@Factory
class EditorManager(
    private val localFilesManager: LocalFilesManager,
    private val remoteFilesManager: RemoteFilesManager,
    private val scriptManager: ScriptManager,
    private val storageManager: StorageManager,
    private val themesManager: ThemesManager,
    private val boardManager: BoardManager
) {

    private lateinit var session: EditorSession

    /** Whether [open] has finished. Until it has, the editor has nothing to show. */
    var isOpen by mutableStateOf(false)
        private set

    /** The configuration for the editor, restored from [storageManager]. */
    lateinit var settings: EditorSettings
        private set

    /** Reads the handed-over script off disk and readies the editor. */
    suspend fun open() {
        if (isOpen) return
        open(
            session = EditorSession.create(scriptManager.scriptToOpen()),
            theme = themesManager.theme
        )
    }

    /** Opens on a session that is already built, for `@Preview`. */
    internal fun open(session: EditorSession, theme: EditorTheme) {
        this.session = session
        this.settings = EditorSettings(
            theme = theme,
            fontSize = storageManager.fontSize,
            showLineNumbers = storageManager.showLineNumbers
        )
        isOpen = true
    }


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
     */
    val canRunScript: Boolean get() = boardManager.isConnected && isPython

    /**
     * Whether the save button is available.
     */
    val canSave: Boolean get() = boardManager.isConnected || isLocal

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
    private val asMicroScript: MicroScript get() = session.asMicroScript

    private val _commands = Channel<EditorCommand>(Channel.BUFFERED)

    /**
     * What the editor wants shown or navigated to.
     *
     * A Channel rather than a SharedFlow so a command raised before anyone starts
     * collecting is buffered instead of dropped.
     */
    val commands: Flow<EditorCommand> = _commands.receiveAsFlow()

    /** The action waiting on a save or a prompt. */
    private var pendingAction: EditorAction? = null

    /**
     * Entry point for every toolbar action and the back gesture.
     *
     * Decides whether the action can go straight through, needs a silent save first,
     * or needs to ask.
     */
    suspend fun onAction(action: EditorAction) {
        // "save as" always asks where, and leaves nothing pending
        if (action == EditorAction.SaveScriptAs) {
            if (saveToFile()) _commands.trySend(EditorCommand.Saved)
            return
        }
        pendingAction = action
        when {
            saveExisting() -> when (action) {
                // A plain save just reports back; nothing is pending afterward.
                EditorAction.SaveScript -> {
                    save()
                    pendingAction = null
                    _commands.trySend(EditorCommand.Saved)
                }
                // Running always uses the latest text, so save first instead of asking.
                EditorAction.RunScript -> {
                    save()
                    finishPendingAction()
                }
                // Closing or starting a new script can discard work, so ask.
                else -> _commands.trySend(EditorCommand.RequestSave)
            }

            // a new script is named and placed in the platform's save dialog
            saveNew() -> {
                val saved = saveToFile()
                if (saved && action == EditorAction.SaveScript) {
                    _commands.trySend(EditorCommand.Saved)
                }
                finishPendingAction()
            }

            else -> finishPendingAction()
        }
    }

    ////// Save prompt

    suspend fun onSaveConfirmed() {
        save()
        finishPendingAction()
    }

    fun onSaveDismissed() {
        finishPendingAction()
    }

    /** Persists settings, then turns the pending action into a command. */
    private fun finishPendingAction() {
        persistSettings()
        val action = pendingAction
        pendingAction = null
        when (action) {
            EditorAction.NewScript -> reset()
            EditorAction.OpenScript -> _commands.trySend(EditorCommand.RequestOpen)
            EditorAction.CloseScript -> _commands.trySend(EditorCommand.Close)
            EditorAction.RunScript -> {
                scriptManager.open(asMicroScript)
                _commands.trySend(EditorCommand.Run)
            }

            else -> {}
        }
    }

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
        if (isOpen) persistSettings()
    }

    /**
     * Shows the file picker and loads what was chosen into the editor.
     *
     * @return false if the picker was dismissed or the file could not be read.
     */
    suspend fun openScriptFile(): Boolean {
        val script = scriptManager.pickAScript() ?: return false
        session.openScript(script)
        return true
    }

    /** Empties the buffer for a new, unnamed script. */
    private fun reset() {
        session.reset()
    }

    /**
     * Saves the buffer to a file picked in the platform's save dialog, which
     * becomes the local script being edited.
     *
     * @return false if the dialog was dismissed or the file could not be written.
     */
    private suspend fun saveToFile(): Boolean {
        // no path, so the dialog always asks where
        val script = asMicroScript.copy(path = "", editorMode = EditorMode.LOCAL)
        val saved = scriptManager.save(script) ?: return false
        session.moveTo(saved)
        session.markSaved()
        AppLog.v(TAG, "saveToFile - ${saved.path}")
        return true
    }

    /** True if the script exists and has unsaved changes. */
    private fun saveExisting(): Boolean = session.isDirty

    /** True if the script is new (no path) and has content. */
    private fun saveNew(): Boolean = session.isNew

    /**
     * Saves the current script locally or to the MicroPython board.
     *
     * Both paths suspend: the managers move the actual IO off the caller's thread.
     */
    private suspend fun save() {
        if (script.isLocal) {
            // the scripts folder first, then the platform's own save dialog for a
            // file that came from outside it
            if (localFilesManager.write(script.path, codeState.code)) {
                session.markSaved()
            } else {
                val saved = scriptManager.save(session.asMicroScript) ?: return
                // the dialog decides the location, so the session follows it there
                session.moveTo(saved.path, saved.name)
                session.markSaved()
            }
        } else {
            remoteFilesManager.write(path = script.path, content = codeState.code)
            session.markSaved()
        }
    }

    /** Cheap enough to stay synchronous: the platform write itself is async. */
    private fun persistSettings() {
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
