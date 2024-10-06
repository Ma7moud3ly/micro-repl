/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


/**
 * This class manages saving and retrieving  scripts locally
 * in the app external storage /storage/android/data/package-name/
 */
class ScriptsManager(private val context: Context) {
    companion object {
        private const val TAG = "ScriptsManager"
    }

    val scripts = mutableStateListOf<MicroScript>()

    init {
        updateScriptsList()
    }

    fun scriptDirectory(): File? {
        if (context.getExternalFilesDir("scripts")?.exists() == false) {
            val outFile = context.getExternalFilesDir("scripts")
            outFile?.mkdirs()
        }
        return context.getExternalFilesDir("scripts")
    }

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


    fun deleteScript(script: MicroScript) {
        val b = delete(script)
        if (b) updateScriptsList()
    }

    fun renameScript(script: MicroScript, newName: String) {
        val b = rename(script, newName)
        if (b) updateScriptsList()
    }


    /**
     * File Methods
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

