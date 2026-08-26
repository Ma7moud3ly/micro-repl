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
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.File
import java.io.IOException

/**
 * Single state facade for the editor screen.
 *
 * The manager owns the Nemo [CodeState] (text + undo/redo history) and
 * [EditorSettings] (theme, line numbers, font size) and exposes the small bit of
 * script identity the UI needs (title, path, script flags, run availability).
 * The UI reads everything from this one object — there is no separate mirror
 * state and no reactive syncing.
 *
 * Use [EditorManager.create] to build an instance for the real screen (it reads
 * persisted preferences and restores the recent script). The primary constructor
 * is intentionally free of `Activity`/filesystem work so it stays usable in
 * `@Preview`.
 */
class EditorManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    val codeState: CodeState,
    val settings: EditorSettings,
    initialScript: MicroScript,
    private val filesManager: FilesManager? = null,
    private val onRun: ((MicroScript) -> Unit)? = null,
    private val afterEdit: (() -> Unit)? = null
) {
    // Built lazily so the constructor stays preview-safe.
    private val scriptsManager by lazy { ScriptsManager(context) }

    private var script: MicroScript = initialScript

    // The last persisted content; the diff against it is our "dirty" signal.
    private var savedContent: String = initialScript.content

    var actionAfterSave: EditorAction? = null

    /** The editor title (file name / path), shown in the header. */
    val title = mutableStateOf(initialScript.path)

    /** Whether the run button is available. */
    val canRun = mutableStateOf(false)

    /**
     * Script identity, derived from the current [script].
     */
    val isLocal: Boolean get() = script.isLocal
    val microPython: Boolean get() = script.microPython
    val isPython: Boolean get() = script.isPython
    val path: String get() = script.path
    val exists: Boolean get() = script.exists || script.path.isNotEmpty()

    /**
     * Undo/redo availability, derived from [codeState]. Reading [codeState.code]
     * (a snapshot state) inside the derivation makes these recompose on every
     * edit/undo/redo.
     */
    val canUndo: Boolean by derivedStateOf {
        codeState.code
        codeState.canUndo()
    }
    val canRedo: Boolean by derivedStateOf {
        codeState.code
        codeState.canRedo()
    }

    /** Line-numbers visibility, owned by [settings]. */
    val showLines: Boolean get() = settings.showLineNumbersState.value

    private val asMicroScript: MicroScript
        get() = MicroScript(
            content = codeState.code,
            path = script.path,
            editorMode = script.editorMode
        )

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
            EditorAction.NewScript -> {
                codeState.updateText("")
                codeState.clearHistory()
                script = MicroScript(editorMode = script.editorMode)
                savedContent = ""
                title.value = context.getString(R.string.editor_untitled)
            }

            EditorAction.CLoseScript -> afterEdit?.invoke()
            EditorAction.RunScript -> onRun?.invoke(asMicroScript)
            else -> {}
        }
    }

    /** True if the script exists and has unsaved changes. */
    fun saveExisting(): Boolean {
        val exist = exists && codeState.code != savedContent
        Log.v(TAG, "saveExisting  - $exist")
        return exist
    }

    /** True if the script is new (no path) and has content. */
    fun saveNew(): Boolean {
        val new = exists.not() && codeState.code.isNotEmpty()
        Log.v(TAG, "saveNew - $new")
        return new
    }

    /**
     * Saves the current script locally or to the MicroPython board.
     */
    fun save(onDone: () -> Unit) {
        if (isLocal) {
            val file = File(script.path)
            val saved = scriptsManager.write(file, codeState.code)
            if (saved) {
                savedContent = codeState.code
                title.value = file.name
            }
            onDone()
        } else {
            filesManager?.write(
                path = script.path,
                content = codeState.code,
                onSave = {
                    savedContent = codeState.code
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
            script = script.copy(path = it.path + "/" + name)
            title.value = name
            Log.v(TAG, "saveFileAs - ${script.path}")
            save(onDone)
        }
    }

    private fun persistSettings() {
        val activity = context as? Activity ?: return
        activity.getPreferences(Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SHOW_LINES, settings.showLineNumbersState.value)
            putInt(KEY_FONT_SIZE, settings.fontSizeState.value)
            if (isLocal && exists) {
                Log.v(TAG, "persistSettings - hasScript")
                putString(KEY_SCRIPT, script.path)
            }
        }
    }

    companion object {
        private const val TAG = "EditorManager"
        private const val KEY_SHOW_LINES = "show_lines"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_SCRIPT = "script"

        /**
         * Builds an [EditorManager] for the real screen: reads persisted settings,
         * restores the recent local script when opening without one, and creates
         * the Nemo [CodeState]/[EditorSettings].
         */
        fun create(
            context: Context,
            coroutineScope: CoroutineScope,
            script: MicroScript,
            blank: Boolean,
            theme: EditorTheme,
            filesManager: FilesManager? = null,
            onRun: ((MicroScript) -> Unit)? = null,
            afterEdit: (() -> Unit)? = null
        ): EditorManager {
            val activity = context as Activity
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            val resolved = restoreRecentScript(context, sharedPref, script, blank)

            val settings = EditorSettings(
                theme = theme,
                // EditorSettings requires fontSize in 8..32.
                fontSize = sharedPref.getInt(KEY_FONT_SIZE, 14).coerceIn(8, 32),
                showLineNumbers = sharedPref.getBoolean(KEY_SHOW_LINES, true)
            )
            val codeState = CodeState(
                initialCode = resolved.content,
                language = Language.MICRO_PYTHON,
            )
            return EditorManager(
                context = context,
                coroutineScope = coroutineScope,
                codeState = codeState,
                settings = settings,
                initialScript = resolved,
                filesManager = filesManager,
                onRun = onRun,
                afterEdit = afterEdit
            )
        }

        /**
         * Returns the recent local script when the editor is opened without a
         * script; otherwise returns [script] unchanged.
         */
        private fun restoreRecentScript(
            context: Context,
            sharedPref: android.content.SharedPreferences,
            script: MicroScript,
            blank: Boolean
        ): MicroScript {
            if (blank || script.isLocal.not() || script.exists) return script
            val recent = sharedPref.getString(KEY_SCRIPT, "").orEmpty()
            if (recent.isEmpty()) return script
            val file = File(recent)
            if (file.exists().not()) return script
            return try {
                script.copy(content = ScriptsManager(context).read(file), path = recent)
            } catch (e: IOException) {
                e.printStackTrace()
                script
            }
        }
    }
}

sealed interface EditorAction {
    data object RunScript : EditorAction
    data object SaveScript : EditorAction
    data object NewScript : EditorAction
    data object CLoseScript : EditorAction
}
