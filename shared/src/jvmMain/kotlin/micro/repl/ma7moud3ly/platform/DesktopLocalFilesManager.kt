/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.Single

/**
 * Reports an empty scripts folder and refuses every read and write.
 */
@Single(binds = [LocalFilesManager::class])
class DesktopLocalFilesManager : LocalFilesManager {

    override val scripts: SnapshotStateList<MicroScript> = mutableStateListOf()

    override suspend fun refresh() = Unit

    override suspend fun scriptDirectory(): String = ""

    override suspend fun deleteScript(script: MicroScript) = Unit

    override suspend fun renameScript(script: MicroScript, newName: String) = Unit

    override fun shareScript(script: MicroScript) = Unit

    override suspend fun exists(path: String): Boolean = false

    override suspend fun read(path: String): String = ""

    override suspend fun write(path: String, data: String): Boolean = false
}
