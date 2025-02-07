/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import micro.repl.ma7moud3ly.model.MicroScript
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
 * The `ScriptsManager` maintains a list of `MicroScript` objects representing
 * the available scripts. This list is updated whenever scripts are added,
 * deleted, or renamed.
 */
class ScriptsManager(private val context: Context) {
    companion object {
        private const val TAG = "ScriptsManager"
    }

    /**
     * A list of `MicroScript` objects representing the available scripts.
     */
    val scripts = mutableStateListOf<MicroScript>()

    init {
        updateScriptsList()
    }

    /**
     * Returns the directory where scripts are stored.
     *
     * Creates the directory if it does not exist.
     *
     * @return The scripts directory, or `null` if it could not be created.
     */
    fun scriptDirectory(): File? {
        if (context.getExternalFilesDir("scripts")?.exists() == false) {
            val outFile = context.getExternalFilesDir("scripts")
            outFile?.mkdirs()
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
        scriptDirectory()?.let { it ->
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
    fun deleteScript(script: MicroScript) {
        val b = delete(script)
        if (b) updateScriptsList()
    }

    /**
     * Renames a script.
     *
     * @param script The `MicroScript` object representing the script to rename.
     * @param newName The new name for the script.
     */
    fun renameScript(script: MicroScript, newName: String) {
        val b = rename(script, newName)
        if (b) updateScriptsList()
    }

    /**
     * Shares a script file with other apps.
     *
     * @param script The MicroScript object representing the script to share.
     */
    fun shareScript(script: MicroScript) {
        val file = script.file
        if (!file.exists()) {
            // Handle the case where the file doesn't exist
            return
        }

        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

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
     * @param file The `File` object representing the script file to read.
     * @return The content of the script file as a string.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    fun read(file: File): String {
        return if (!file.exists()) "" else try {
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
     * @param file The `File` object representing the script file to write to.
     * @param data The data to write to the file as a string.
     * @return `true` if the write operation was successful, `false` otherwise.
     */
    fun write(file: File, data: String): Boolean {
        if (file.parentFile?.exists() == false) file.mkdirs()
        return try {
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
        val file = script.file
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
        val newFile = File(script.file.parentFile, newName)
        val oldFile = script.file
        if (!oldFile.exists()) return false
        return try {
            oldFile.renameTo(newFile)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}