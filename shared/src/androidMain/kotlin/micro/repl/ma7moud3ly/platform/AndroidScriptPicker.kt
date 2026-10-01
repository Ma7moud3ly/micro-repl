/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.fromBookmarkData
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.releaseBookmark
import io.github.vinceglb.filekit.writeString
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.Single
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
@Single(binds = [ScriptPicker::class])
class AndroidScriptPicker : ScriptPicker {
    private var openedFile: PlatformFile? = null

    override suspend fun open(): MicroScript? {
        val file = FileKit.openFilePicker(type = FileKitType.File(ScriptTypes)) ?: return null
        return try {
            val script = MicroScript(
                path = file.absolutePath(),
                initialName = file.name,
                content = file.readString(),
                editorMode = EditorMode.LOCAL
            )
            openedFile = file
            script
        } catch (e: Exception) {
            AppLog.e(TAG, "open - cannot read ${file.name}", e)
            null
        }
    }

    override suspend fun save(script: MicroScript): MicroScript? {
        // only the file this script came from is written without asking
        val file = openedFile?.takeIf { it.absolutePath() == script.path }
            ?: FileKit.openFileSaver(
                suggestedName = script.nameWithoutExt.ifEmpty { "main" },
                defaultExtension = "py",
                directory = null
            )
            ?: return null
        return try {
            file.writeString(script.content)
            openedFile = file
            script.copy(path = file.absolutePath(), initialName = file.name)
        } catch (e: Exception) {
            AppLog.e(TAG, "save - cannot write ${file.name}", e)
            null
        }
    }

    override suspend fun bookmark(): String? {
        val file = openedFile ?: return null
        return try {
            Base64.encode(file.bookmarkData().bytes)
        } catch (e: Exception) {
            AppLog.e(TAG, "bookmark - cannot keep ${file.name}", e)
            null
        }
    }

    override suspend fun reopen(bookmark: String): MicroScript? = try {
        val file = PlatformFile.fromBookmarkData(Base64.decode(bookmark))
        val script = MicroScript(
            path = file.absolutePath(),
            initialName = file.name,
            content = file.readString(),
            editorMode = EditorMode.LOCAL
        )
        openedFile = file
        script
    } catch (e: Exception) {
        AppLog.e(TAG, "reopen - cannot read the bookmarked file", e)
        null
    }

    override fun forget(bookmark: String) {
        try {
            PlatformFile.fromBookmarkData(Base64.decode(bookmark)).releaseBookmark()
        } catch (e: Exception) {
            AppLog.e(TAG, "forget - cannot release the bookmark", e)
        }
    }
}
