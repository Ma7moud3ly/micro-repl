/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import micro.repl.ma7moud3ly.model.MicroFile
import org.json.JSONArray
import org.koin.core.annotation.Single


/**
 * Used by Files Explorer to manage file and directory operations on a MicroPython board.
 *
 * This class provides methods for listing, creating, deleting, renaming,
 * reading, and writing files and directories on a MicroPython board.
 * It interacts with the `BoardManager` to send commands and receive responses
 * from the board's REPL.
 *
 * The `FilesManager` uses the `CommandsManager` to generate the appropriate
 * MicroPython commands for each operation. It also handles decoding the
 * responses from the board to extract file and directory information.
 *
 * @param replManager The `ReplManager` instance used to communicate with the board.
 */
@Single
class FilesManager(
    private val replManager: ReplManager
) {

    private val _files = MutableStateFlow<List<MicroFile>>(emptyList())

    /** Contents of the directory last listed by [listDir]. */
    val files: StateFlow<List<MicroFile>> = _files.asStateFlow()

    companion object {
        private const val TAG = "FilesManager"
    }


    /**
     * The current working directory path on the MicroPython board.
     */
    private var path = ""

    /**
     * Lists the files and directories in the current working directory.
     *
     * This method sends a command to the MicroPython board to list the contents
     * of the current working directory. The response from the board is then
     * decoded into a list of `MicroFile` objects, which is then passed to the
     * [files] flow.
     */
    suspend fun listDir(path: String) {
        Log.v(TAG, "path: $path")
        this.path = path
        val code = CommandsManager.listDir(path)
        decodeFiles(replManager.writeInSilentMode(code))
    }

    /**
     * Lists the files and directories in the current working directory.
     * This method calls the overloaded `listDir` method with the current path.
     */
    suspend fun listDir() {
        listDir(this.path)
    }

    /**
     * Removes a file or directory.
     *
     * @param file The `MicroFile` object representing the file or directory to remove.
     */
    suspend fun remove(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.removeFile(file)
        else CommandsManager.removeDirectory(file)
        decodeFiles(replManager.writeInSilentMode(code))
    }

    /**
     * Creates a new file or directory.
     *
     * @param file The `MicroFile` object representing the file or directory to create.
     */
    suspend fun new(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.makeFile(file)
        else CommandsManager.makeDirectory(file)
        decodeFiles(replManager.writeInSilentMode(code))
    }

    /**
     * Renames a file or directory.
     *
     * @param src The `MicroFile` object representing the source file or directory.
     * @param dst The `MicroFile` object representing the destination file or directory.
     */
    suspend fun rename(src: MicroFile, dst: MicroFile) {
        val code = CommandsManager.rename(src, dst)
        decodeFiles(replManager.writeInSilentMode(code))
    }

    /**
     * Reads the contents of a file.
     *
     * @param path The path to the file to read.
     * @return The contents of the file.
     */
    suspend fun read(path: String): String {
        val code = CommandsManager.readFile(path)
        return replManager.writeInSilentMode(code)
    }

    /**
     * Writes content to a file.
     *
     * @param path The path to the file to write to.
     * @param content The content to write to the file.
     */
    suspend fun write(path: String, content: String) {
        val code = CommandsManager.writeFile(path, content)
        val result = replManager.writeInSilentMode(code)
        Log.i(TAG, "result $result")
    }

    /**
     * Writes binary content to a file.
     *
     * @param path The path to the file to write to.
     * @param bytes The bytes to write to the file.
     */
    suspend fun writeBinary(path: String, bytes: ByteArray) {
        Log.v(TAG, "writeBinary-to: $path")
        val code = CommandsManager.writeBinaryFile(path, bytes)
        val result = replManager.writeInSilentMode(code)
        Log.i(TAG, "result $result")
    }

    /**
     * Decodes the JSON response from the board manager into a list of `MicroFile` objects.
     *
     * This method parses the JSON response received from the MicroPython board
     * and creates a list of `MicroFile` objects representing the files and
     * directories in the current working directory. objects are sorted to show directories
     * first then files.Finally, the list is passed to the [files] flow.
     *
     * @param json The JSON response string received from the board manager.
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
            val item = items[i] as? JSONArray ?: continue
            val length = item.length()

            if (length >= 3) {
                val name = (item[0] as? String).orEmpty()
                val type = (item[1] as? Int) ?: 0x8000
                val size = if (length == 4) ((item[3] as? Int) ?: 0)
                else 0
                list.add(MicroFile(name = name, path = this.path, type = type, size = size))
            }
        }
        val sortedFiles = list.sortedBy { file ->
            if (file.isDIRECTORY) {
                0 // Prioritized directories comes first
            } else {
                1 // Other files come after
            }
        }
        Log.i(TAG, sortedFiles.toString())
        _files.value = sortedFiles
    }
}
