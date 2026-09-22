/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.Language
import micro.repl.ma7moud3ly.managers.port.ScriptsManager
import micro.repl.ma7moud3ly.managers.port.StorageManager
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.File
import java.io.IOException

/**
 * What the editor is working on: the buffer, the file it belongs to, and the
 * baseline used to tell whether there are unsaved changes.
 *
 */
class EditorSession(
    val codeState: CodeState,
    initialScript: MicroScript
) {
    /** The file being edited. Changes on "save as" and "new". */
    var script by mutableStateOf(initialScript)
        private set

    /** Content as of the last successful save; the baseline for [isDirty]. */
    var savedContent by mutableStateOf(initialScript.content)
        private set

    /**
     * A saved script with edits that haven't been written back yet.
     *
     * Derived rather than a plain getter so readers wake only when the flag
     * itself flips. A getter would subscribe them to `codeState.code` and
     * recompose on every keystroke, even though this stays true throughout.
     */
    val isDirty: Boolean by derivedStateOf {
        script.exists && codeState.code != savedContent
    }

    /** A script with content but no path yet, so it needs a name before saving. */
    val isNew: Boolean get() = script.exists.not() && codeState.code.isNotEmpty()

    /** The buffer as a script: the live text, with the path and mode it belongs to. */
    val asMicroScript: MicroScript
        get() = MicroScript(
            content = codeState.code,
            path = script.path,
            editorMode = script.editorMode
        )

    /** Records a successful save, which clears [isDirty]. */
    fun markSaved() {
        savedContent = codeState.code
    }

    /** Points the session at a new path, for "save as". */
    fun moveTo(path: String) {
        script = script.copy(path = path)
    }

    /** Empties the editor for a new, unnamed script, keeping the current mode. */
    fun reset() {
        codeState.updateText("")
        codeState.clearHistory()
        script = MicroScript(editorMode = script.editorMode)
        savedContent = ""
    }

    companion object {

        /**
         * Builds a session for [script], restoring the most recent local script
         * when the editor is opened without one.
         */
        suspend fun create(
            script: MicroScript,
            blank: Boolean,
            scriptsManager: ScriptsManager,
            storageManager: StorageManager
        ): EditorSession {
            val resolved = restoreRecentScript(script, blank, scriptsManager, storageManager)
            return EditorSession(
                codeState = CodeState(
                    initialCode = resolved.content,
                    language = Language.MICRO_PYTHON
                ),
                initialScript = resolved
            )
        }

        /**
         * Returns the recent local script when the editor is opened without a
         * script; otherwise returns [script] unchanged.
         */
        private suspend fun restoreRecentScript(
            script: MicroScript,
            blank: Boolean,
            scriptsManager: ScriptsManager,
            storageManager: StorageManager
        ): MicroScript {
            if (blank || script.isLocal.not() || script.exists) return script
            val recent = storageManager.recentScript
            if (recent.isEmpty()) return script
            val file = File(recent)
            if (file.exists().not()) return script
            return try {
                script.copy(content = scriptsManager.read(file), path = recent)
            } catch (e: IOException) {
                e.printStackTrace()
                script
            }
        }
    }
}
