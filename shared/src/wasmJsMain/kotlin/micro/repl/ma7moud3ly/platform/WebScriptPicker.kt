/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.download
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readString
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.Single


/**
 * [ScriptPicker] over the browser's file input, saving through a download.
 */
@Single(binds = [ScriptPicker::class])
class WebScriptPicker : ScriptPicker {
    override suspend fun open(): MicroScript? {
        val file = FileKit.openFilePicker(type = FileKitType.File(ScriptTypes)) ?: return null
        return try {
            MicroScript(
                path = file.path,
                initialName = file.name,
                content = file.readString(),
                editorMode = EditorMode.LOCAL
            )
        } catch (e: Exception) {
            AppLog.e(TAG, "open - cannot read ${file.name}", e)
            null
        }
    }

    /** Hands the script to the browser as a download; the original file is untouched. */
    override suspend fun save(script: MicroScript): MicroScript? {
        val name = script.name.ifEmpty { "main.py" }
        return try {
            FileKit.download(bytes = script.content.encodeToByteArray(), fileName = name)
            script.copy(path = name, initialName = name)
        } catch (e: Exception) {
            AppLog.e(TAG, "save - cannot download $name", e)
            null
        }
    }

    /** The browser keeps no access to a file between sessions. */
    override suspend fun bookmark(): String? = null

    override suspend fun reopen(bookmark: String): MicroScript? = null

    override fun forget(bookmark: String) = Unit
}
