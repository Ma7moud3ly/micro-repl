/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import micro.repl.ma7moud3ly.managers.port.LocalFilesManager
import micro.repl.ma7moud3ly.managers.port.StorageManager
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.Single
import java.io.File
import java.io.IOException

/**
 * The script being handed from one screen to the next, and which one the editor
 * should end up on.
 *
 */
@Single
class ScriptManager(
    private val localFilesManager: LocalFilesManager,
    private val storageManager: StorageManager
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
        val file = File(recent)
        if (file.exists().not()) return script
        return try {
            script.copy(content = localFilesManager.read(file), path = recent)
        } catch (e: IOException) {
            e.printStackTrace()
            script
        }
    }
}
