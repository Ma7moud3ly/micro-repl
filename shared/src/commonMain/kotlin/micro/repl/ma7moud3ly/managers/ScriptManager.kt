/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import micro.repl.ma7moud3ly.platform.LocalFilesManager
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.RecentScript
import micro.repl.ma7moud3ly.platform.AppLog
import micro.repl.ma7moud3ly.platform.ScriptPicker
import org.koin.core.annotation.Single

/**
 * The script being handed from one screen to the next, and which one the editor
 * should end up on.
 *
 */
@Single
class ScriptManager(
    private val localFilesManager: LocalFilesManager,
    private val storageManager: StorageManager,
    private val scriptPicker: ScriptPicker
) {

    /** The script the next screen should work on. */
    var script: MicroScript = MicroScript()
        private set

    /** Whether the editor should start empty rather than restoring the last script. */
    var blank: Boolean = false
        private set

    /** Hands [script] to the next screen. Pass `MicroScript()` for a bare session. */
    fun open(script: MicroScript, blank: Boolean = false) {
        this.script = script
        this.blank = blank
        AppLog.v(TAG, script.path)
    }

    /**
     * The script the editor should open on.
     *
     * Usually the one that was handed over, but an editor opened without a script
     * picks up where the last local one left off - unless [blank] asked for an
     * empty buffer.
     */
    suspend fun scriptToOpen(): MicroScript {
        if (blank || script.isLocal.not() || script.exists) return script
        val recent = storageManager.recentScript
        if (recent.isEmpty()) return script
        if (localFilesManager.exists(recent).not()) return script
        return try {
            script.copy(content = localFilesManager.read(recent), path = recent)
        } catch (e: Exception) {
            e.printStackTrace()
            script
        }
    }

    /**
     * Shows the file picker and hands what was chosen to the editor.
     */
    suspend fun pickAScript(): MicroScript? {
        val script = scriptPicker.open() ?: return null
        open(script)
        addRecent(script)
        return script
    }

    /**
     * Show script save as dialog
     */
    suspend fun save(script: MicroScript): MicroScript? {
        val saved = scriptPicker.save(script) ?: return null
        addRecent(saved)
        return saved
    }

    ////// Recent scripts

    /** Files opened or saved through the file dialogs, newest first. */
    val recentScripts: SnapshotStateList<RecentScript> =
        storageManager.recentScripts.toMutableStateList()

    /**
     * Reads [recent] back and hands it to the next screen, moving it to the top.
     * A file that is gone is dropped from [recentScripts].
     *
     * @return the script, or null if it could not be read.
     */
    suspend fun openRecent(recent: RecentScript): MicroScript? {
        val script = scriptPicker.reopen(recent.bookmark)
        if (script == null) {
            removeRecent(recent)
            return null
        }
        open(script)
        recentScripts.remove(recent)
        recentScripts.add(0, recent)
        storageManager.recentScripts = recentScripts
        return script
    }

    /** Drops [recent] from the list and gives up the access it kept. */
    fun removeRecent(recent: RecentScript) {
        scriptPicker.forget(recent.bookmark)
        recentScripts.remove(recent)
        storageManager.recentScripts = recentScripts
    }

    /**
     * Puts [script], the file just opened or saved, at the top of
     * [recentScripts], keeping at most [MAX_RECENT_SCRIPTS].
     */
    private suspend fun addRecent(script: MicroScript) {
        val bookmark = scriptPicker.bookmark() ?: return
        recentScripts.removeAll { it.path == script.path }
        recentScripts.add(0, RecentScript(script.name, script.path, bookmark))
        while (recentScripts.size > MAX_RECENT_SCRIPTS) {
            scriptPicker.forget(recentScripts.removeAt(recentScripts.lastIndex).bookmark)
        }
        storageManager.recentScripts = recentScripts
    }

    companion object {
        private const val TAG = "ScriptManager"
        private const val MAX_RECENT_SCRIPTS = 15
    }
}
