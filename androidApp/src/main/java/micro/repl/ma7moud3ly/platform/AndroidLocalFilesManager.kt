/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.managers.port.LocalFilesManager
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.Single
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


/**
 * Manages the saving and retrieval of scripts locally on the device.
 *
 * This class handles the storage and management of scripts in the
 * application's external storage directory. It provides methods for creating,
 * reading, writing, deleting, and renaming script files. The scripts are stored
 * in the directory `/storage/emulated/0/Android/data/package-name/files/scripts/`.
 *
 * The `AndroidScriptsManager` maintains a list of `MicroScript` objects representing
 * the available scripts. This list is updated whenever scripts are added,
 * deleted, or renamed.
 */
@Single(binds = [LocalFilesManager::class])
class AndroidLocalFilesManager(private val context: Context) : LocalFilesManager {
    companion object {
        private const val TAG = "ScriptsManager"
    }

    /**
     * A list of `MicroScript` objects representing the available scripts.
     */
    override val scripts = mutableStateListOf<MicroScript>()


    override suspend fun refresh() {
        withContext(Dispatchers.IO) { updateScriptsList() }
    }

    /**
     * Returns the directory where scripts are stored.
     *
     * Creates the directory if it does not exist.
     *
     * @return The scripts directory, or `null` if it could not be created.
     */
    override suspend fun scriptDirectory(): String =
        withContext(Dispatchers.IO) { scriptsDir()?.absolutePath.orEmpty() }

    /** The same lookup, for the IO-confined helpers below. */
    private fun scriptsDir(): File? {
        if (context.getExternalFilesDir("scripts")?.exists() == false) {
            context.getExternalFilesDir("scripts")?.mkdirs()
        }
        return context.getExternalFilesDir("scripts")
    }

    /**
     * Updates the list of available scripts.
     *
     * Scans the scripts directory and updates the `scripts` list with the
     * found scripts.
     */
    private fun updateScriptsList() {
        val list = mutableListOf<MicroScript>()
        scriptsDir()?.let { it ->
            it.listFiles()?.forEach { file ->
                val path = file.absolutePath
                val script = MicroScript(path = path)
                list.add(script)
            }
        }
        scripts.clear()
        scripts.addAll(list)
    }

    /**
     * Deletes a script.
     *
     * @param script The `MicroScript` object representing the script to delete.
     */
    override suspend fun deleteScript(script: MicroScript) {
        withContext(Dispatchers.IO) {
            val b = delete(script)
            if (b) updateScriptsList()
        }
    }

    /**
     * Renames a script.
     *
     * @param script The `MicroScript` object representing the script to rename.
     * @param newName The new name for the script.
     */
    override suspend fun renameScript(script: MicroScript, newName: String) {
        withContext(Dispatchers.IO) {
            val b = rename(script, newName)
            if (b) updateScriptsList()
        }
    }

    /**
     * Shares a script file with other apps.
     *
     * @param script The MicroScript object representing the script to share.
     */
    override fun shareScript(script: MicroScript) {
        val file = File(script.path)
        if (!file.exists()) {
            // Handle the case where the file doesn't exist
            return
        }

        // getUriForFile throws when the file falls outside every root declared
        // in provider_paths.xml, which varies with how a device mounts external
        // storage. Sharing failing is not worth crashing over.
        val fileUri: Uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "shareScript - cannot expose ${file.absolutePath}", e)
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${script.name}"))
    }

    /**
     * Reads the content of a script file.
     *
     * @param path Absolute path of the script file to read.
     * @return The content of the script file as a string.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    override suspend fun exists(path: String): Boolean =
        withContext(Dispatchers.IO) { File(path).exists() }

    override suspend fun read(path: String): String = withContext(Dispatchers.IO) {
        val file = File(path)
        if (!file.exists()) "" else try {
            val dis = DataInputStream(FileInputStream(file))
            val byt = ByteArray(dis.available())
            dis.readFully(byt)
            dis.close()
            String(byt, 0, byt.size)
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException()
        }
    }

    /**
     * Writes data to a script file.
     *
     * @param path Absolute path of the script file to write to.
     * @param data The data to write to the file as a string.
     * @return `true` if the write operation was successful, `false` otherwise.
     */
    override suspend fun write(path: String, data: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.parentFile?.exists() == false) file.mkdirs()
        try {
            if (file.exists().not()) file.createNewFile()
            val out = FileOutputStream(file)
            val writer = OutputStreamWriter(out)
            writer.append(data)
            writer.flush()
            writer.close()
            out.close()
            true
        } catch (e: Exception) {
            file.delete()
            e.printStackTrace()
            false
        }
    }

    /**
     * Deletes a script file.
     *
     * @param script The `MicroScript` object representing the script file to delete.
     * @return `true` if the delete operation was successful, `false` otherwise.
     */
    private fun delete(script: MicroScript): Boolean {
        val file = File(script.path)
        return if (!file.exists()) false
        else try {
            return file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Renames a script file.
     *
     * @param script The `MicroScript` object representing the script file to rename.
     * @param newName The new name for the script file.
     * @return `true` if the rename operation was successful, `false` otherwise.
     */
    private fun rename(script: MicroScript, newName: String): Boolean {
        val oldFile = File(script.path)
        val newFile = File(oldFile.parentFile, newName)
        return oldFile.exists() && try {
            oldFile.renameTo(newFile)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}