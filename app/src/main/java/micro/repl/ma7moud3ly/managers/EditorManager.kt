/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.core.content.edit
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.File


/**
 * Manages the state and actions of the code editor.
 *
 * This class handles editor settings, code execution, file operations (save, undo/redo),
 * and coordinates between the UI and the underlying [EditorSession].
 *
 * @param context The Android context.
 * @param coroutineScope The scope for running asynchronous operations like file I/O.
 * @param session The current editor session containing code and script metadata.
 * @param filesManager Optional manager for remote file operations on a MicroPython board.
 * @param runnable A function that returns true if the script can currently be executed.
 * @param settings The configuration for the editor (theme, font size, etc.).
 * @param onRun Callback invoked when the user requests to run the script.
 * @param afterEdit Callback invoked after a script is closed or edit is finished.
 */
class EditorManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val session: EditorSession,
    private val filesManager: FilesManager? = null,
    private val runnable: () -> Boolean = { false },
    val settings: EditorSettings,
    private val onRun: ((MicroScript) -> Unit)? = null,
    private val afterEdit: (() -> Unit)? = null
) {
    // Built lazily so the constructor stays preview-safe.
    private val scriptsManager by lazy { ScriptsManager(context) }

    val codeState: CodeState get() = session.codeState
    val script: MicroScript get() = session.script
    val isLocal: Boolean get() = script.isLocal
    val isMicroPython: Boolean get() = script.microPython
    val isPython: Boolean get() = script.isPython
    val scriptName: String get() = script.name

    /** The editor title (file name / path), shown in the header. */
    val title: State<String> = derivedStateOf { script.displayName }

    var actionAfterSave: EditorAction? = null

    /**
     * Whether the run button is available.
     *
     * Derived rather than mirrored: reading it in composition tracks the
     * connection state directly, so it goes false again on disconnect.
     */
    val canRun: Boolean get() = runnable()

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

    private val asMicroScript: MicroScript get() = session.asMicroScript

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

    /**
     * Executes the pending [actionAfterSave] (typically after a save completes).
     */
    fun actionAfterSave() {
        Log.v(TAG, "actionAfterSave")
        persistSettings()
        val action = this.actionAfterSave
        actionAfterSave = null
        when (action) {
            EditorAction.NewScript -> session.reset()

            EditorAction.CLoseScript -> afterEdit?.invoke()
            EditorAction.RunScript -> onRun?.invoke(asMicroScript)
            else -> {}
        }
    }

    /** True if the script exists and has unsaved changes. */
    fun saveExisting(): Boolean = session.isDirty

    /** True if the script is new (no path) and has content. */
    fun saveNew(): Boolean = session.isNew

    /**
     * Saves the current script locally or to the MicroPython board.
     *
     * The local write is real file I/O, so it runs off the main thread; [onDone]
     * is always invoked on Main, matching the remote path.
     */
    fun save(onDone: () -> Unit) {
        if (script.isLocal) {
            val file = File(script.path)
            val content = codeState.code
            coroutineScope.launch {
                val saved = withContext(Dispatchers.IO) { scriptsManager.write(file, content) }
                if (saved) session.markSaved()
                onDone()
            }
        } else {
            filesManager?.write(
                path = script.path,
                content = codeState.code,
                onSave = {
                    session.markSaved()
                    coroutineScope.launch {
                        withContext(Dispatchers.Main) {
                            onDone()
                        }
                    }
                }
            )
        }
    }

    /**
     * Saves the current script under a new file name.
     */
    fun saveFileAs(name: String, onDone: () -> Unit) {
        scriptsManager.scriptDirectory()?.let {
            session.moveTo(it.path + "/" + name)
            Log.v(TAG, "saveFileAs - ${script.path}")
            save(onDone)
        }
    }

    private fun persistSettings() {
        val activity = context as? Activity ?: return
        activity.getPreferences(Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SHOW_LINES, settings.showLineNumbersState.value)
            putInt(KEY_FONT_SIZE, settings.fontSizeState.value)
            if (script.isLocal && script.exists) {
                Log.v(TAG, "persistSettings - hasScript")
                putString(KEY_SCRIPT, script.path)
            }
        }
    }

    companion object {
        private const val TAG = "EditorManager"
        private const val KEY_SHOW_LINES = "show_lines"
        private const val KEY_FONT_SIZE = "font_size"
        internal const val KEY_SCRIPT = "script"

        /**
         * Builds an [EditorManager] around an existing [session].
         *
         * The session is passed in rather than created here so the caller can
         * `retain` it across a rotation; everything built here (scope, callbacks,
         * the FilesManager) is tied to the current Activity and must not be.
         */
        fun create(
            context: Context,
            coroutineScope: CoroutineScope,
            session: EditorSession,
            theme: EditorTheme,
            runnable: () -> Boolean = { false },
            filesManager: FilesManager? = null,
            onRun: ((MicroScript) -> Unit)? = null,
            afterEdit: (() -> Unit)? = null
        ): EditorManager {
            val activity = context as Activity
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            val settings = EditorSettings(
                theme = theme,
                // EditorSettings requires fontSize in 8..32.
                fontSize = sharedPref.getInt(KEY_FONT_SIZE, 14).coerceIn(8, 32),
                showLineNumbers = sharedPref.getBoolean(KEY_SHOW_LINES, true)
            )
            return EditorManager(
                context = context,
                coroutineScope = coroutineScope,
                session = session,
                settings = settings,
                filesManager = filesManager,
                runnable = runnable,
                onRun = onRun,
                afterEdit = afterEdit
            )
        }
    }
}

sealed interface EditorAction {
    data object RunScript : EditorAction
    data object SaveScript : EditorAction
    data object NewScript : EditorAction
    data object CLoseScript : EditorAction
}
