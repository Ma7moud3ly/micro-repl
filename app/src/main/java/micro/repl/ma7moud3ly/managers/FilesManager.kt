/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log
import micro.repl.ma7moud3ly.model.MicroFile
import org.json.JSONArray


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
 * @param boardManager The `BoardManager` instance used to communicate with the board.
 * @param onUpdateFiles A callback function that is invoked when the list of files
 *                       in the current directory is updated. This function receives
 *                       a list of `MicroFile` objects representing the files and
 *                       directories in the current directory.
 */
class FilesManager(
    private val boardManager: BoardManager,
    private val onUpdateFiles: ((files: List<MicroFile>) -> Unit)? = null
) {

    companion object {
        private const val TAG = "FileManager"
    }


    /**
     * The current working directory path on the MicroPython board.
     */
    var path = ""

    /**
     * Lists the files and directories in the current working directory.
     *
     * This method sends a command to the MicroPython board to list the contents
     * of the current working directory. The response from the board is then
     * decoded into a list of `MicroFile` objects, which is then passed to the
     * `onUpdateFiles` callback function if it is set.
     */
    fun listDir() {
        val code = CommandsManager.iListDir(path)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Removes a file or directory.
     *
     * @param file The `MicroFile` object representing the file or directory to remove.
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
     * @param file The `MicroFile` object representing the file or directory to create.
     */
    fun new(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.makeFile(file)
        else CommandsManager.makeDirectory(file)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Renames a file or directory.
     *
     * @param src The `MicroFile` object representing the source file or directory.
     * @param dst The `MicroFile` object representing the destination file or directory.
     */
    fun rename(src: MicroFile, dst: MicroFile) {
        val code = CommandsManager.rename(src, dst)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            decodeFiles(result)
        })
    }

    /**
     * Reads the contents of a file.
     *
     * @param path The path to the file to read.
     * @param onRead A callback function that is invoked with the contents of the file.
     */
    fun read(path: String, onRead: (content: String) -> Unit) {
        val code = CommandsManager.readFile(path)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            onRead.invoke(result)
        })
    }

    /**
     * Writes content to a file.
     *
     * @param path The path to the file to write to.
     * @param content The content to write to the file.
     * @param onSave A callback function that is invoked when the write operation is complete.
     */
    fun write(path: String, content: String, onSave: () -> Unit) {
        val code = CommandsManager.writeFile(path, content)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            Log.i(TAG, "result $result")
            onSave.invoke()
        })
    }

    /**
     * Writes binary content to a file.
     *
     * @param path The path to the file to write to.
     * @param bytes The bytes to write to the file.
     * @param onSave A callback function that is invoked when the write operation is complete.
     */
    fun writeBinary(path: String, bytes: ByteArray, onSave: () -> Unit) {
        Log.v(TAG, "writeBinary-to: $path")
        val code = CommandsManager.writeBinaryFile(path, bytes)
        boardManager.writeInSilentMode(code, onResponse = { result ->
            Log.i(TAG, "result $result")
            onSave.invoke()
        })
    }

    /**
     * Decodes the JSON response from the board manager into a list of `MicroFile` objects.
     *
     * This method parses the JSON response received from the MicroPython board
     * and creates a list of `MicroFile` objects representing the files and
     * directories in the current working directory. objects are sorted to show directories
     * first then files.Finally the list is passed to the `onUpdateFiles` callback function if it is set.
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
        onUpdateFiles?.invoke(sortedFiles)
    }
}
