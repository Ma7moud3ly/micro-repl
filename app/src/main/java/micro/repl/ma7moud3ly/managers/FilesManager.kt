/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log
import micro.repl.ma7moud3ly.utils.MicroFile
import org.json.JSONArray


/**
 * This class manages our micro file explorer
 * is has methods to list files/directories and decode them as MicroFile list
 * also it has methods to rename/remove/create files and folders
 */
class FilesManager(
    private val boardManager: BoardManager,
    private val onUpdateFiles: ((files: List<MicroFile>) -> Unit)? = null
) {

    companion object {
        private const val TAG = "FileManager"
    }

    /**
     * The current path being displayed.
     */
    var path = ""

    /**
     * Lists the files and directories in the current path.
     */
    fun listDir() {
        val code = CommandsManager.iListDir(path)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Removes the specified file or directory.
     *
     * @param file The file or directory to remove.
     */
    fun remove(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.removeFile(file)
        else CommandsManager.removeDirectory(file)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Creates a new file or directory.
     *
     * @param file The file or directory to create.
     */
    fun new(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.makeFile(file)
        else CommandsManager.makeDirectory(file)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Renames the specified file or directory.
     *
     * @param src The original file or directory.
     * @param dst The new name for the file or directory.
     */
    fun rename(src: MicroFile, dst: MicroFile) {
        val code = CommandsManager.rename(src, dst)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Reads the contents of the specified file.
     *
     * @param path The path to the file.
     * @param onRead A callback function that will be invoked with the contents of the file.
     */
    fun read(path: String, onRead: (content: String) -> Unit) {
        val code = CommandsManager.readFile(path)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            onRead.invoke(result)
        })
    }

    /**
     * Writes the specified content to the specified file.
     *
     * @param path The path to the file.
     * @param content The content to write to the file.
     * @param onSave A callback function that will be invoked when the file has been saved.
     */
    fun write(path: String, content: String, onSave: () -> Unit) {
        val code = CommandsManager.writeFile(path, content)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            Log.i(TAG, "result $result")
            onSave.invoke()
        })
    }

    /**
     * Decodes the JSON response from the board manager into a list of MicroFile objects.
     *
     * @param json The JSON response from the board manager.
     */
    private fun decodeFiles(json: String) {
        val list = mutableListOf<MicroFile>()
        val jsonFormated = json.replace("(", "[").replace(")", "]")
        val items: JSONArray?
        try {
            items = JSONArray(jsonFormated)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        for (i in 0 until items.length()) {
            val item = items[i] as? JSONArray
            if (item?.length() == 4) {
                val name = (item[0] as? String) ?: ""
                val type = (item[1] as? Int) ?: 0x8000
                val size = (item[3] as? Int) ?: 0
                list.add(MicroFile(name = name, path = this.path, type = type, size = size))
            }
        }
        Log.i(TAG, list.toString())
        onUpdateFiles?.invoke(list)
    }
}
