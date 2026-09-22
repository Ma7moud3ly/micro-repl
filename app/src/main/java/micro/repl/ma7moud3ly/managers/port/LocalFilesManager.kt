/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers.port

import androidx.compose.runtime.snapshots.SnapshotStateList
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.File

/**
 * Scripts stored on the device the app runs on, as opposed to the ones on the board.
 *
 * Where those scripts live, and what "sharing" one means, is decided entirely by the
 * platform: an app-private external directory and a chooser Intent on Android, the
 * user's documents folder and a share sheet elsewhere.
 *
 * Everything that touches the filesystem suspends, so callers never have to know
 * which thread the platform needs for it.
 */
interface LocalFilesManager {

    /** Scripts found by the last [refresh]. Updated again on delete and rename. */
    val scripts: SnapshotStateList<MicroScript>

    /** Rescans the scripts directory. */
    suspend fun refresh()

    /** The directory scripts are stored in, created if missing. */
    suspend fun scriptDirectory(): File?

    suspend fun deleteScript(script: MicroScript)

    suspend fun renameScript(script: MicroScript, newName: String)

    /** Hands the script to whatever sharing mechanism the platform offers. */
    fun shareScript(script: MicroScript)

    suspend fun read(file: File): String

    suspend fun write(file: File, data: String): Boolean
}
