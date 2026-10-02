/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.platform.AppDispatchers
import micro.repl.ma7moud3ly.platform.AppLog
import org.koin.core.annotation.Single


/**
 * The board's filesystem, as the Files Explorer sees it: listing, creating,
 * deleting, renaming, reading and writing files and directories.
 *
 * There is no file protocol on the wire. Every operation is a snippet of
 * MicroPython from [CommandsManager], run silently through [replManager], whose
 * printed reply is decoded back into [MicroFile]s.
 *
 * [listDir] publishes what it finds to [files]; everything else answers its caller
 * directly.
 *
 * @param replManager Runs the snippets on the board and hands back what they printed.
 */
@Single
class RemoteFilesManager(
    private val replManager: ReplManager,
    private val dispatchers: AppDispatchers
) {

    private val _files = MutableStateFlow<List<MicroFile>>(emptyList())

    /** Contents of the directory last listed by [listDir]. */
    val files: StateFlow<List<MicroFile>> = _files.asStateFlow()

    companion object {
        private const val TAG = "RemoteFilesManager"

        /** The size of bytes [writeBinary] sends to the board in one write. */
        private const val CHUNK_SIZE = 8 * 1024
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
        AppLog.v(TAG, "path: $path")
        this.path = path
        val code = CommandsManager.listDir(path)
        // a board that gives no listing has nothing to show
        _files.value = decodeFiles(replManager.writeInSilentMode(code)) ?: emptyList()
    }

    /** Empties [files], for a listing that belongs to another session. */
    fun clear() {
        _files.value = emptyList()
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
        updateFiles(replManager.writeInSilentMode(code))
    }

    /**
     * Creates a new file or directory.
     *
     * @param file The `MicroFile` object representing the file or directory to create.
     */
    suspend fun new(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.makeFile(file)
        else CommandsManager.makeDirectory(file)
        updateFiles(replManager.writeInSilentMode(code))
    }

    /**
     * Renames a file or directory.
     *
     * @param src The `MicroFile` object representing the source file or directory.
     * @param dst The `MicroFile` object representing the destination file or directory.
     */
    suspend fun rename(src: MicroFile, dst: MicroFile) {
        val code = CommandsManager.rename(src, dst)
        updateFiles(replManager.writeInSilentMode(code))
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
        AppLog.i(TAG, "result $result")
    }

    /**
     * Writes binary content to a file.
     *
     * @param path The path to the file to write to.
     * @param bytes The bytes to write to the file.
     */
    suspend fun writeBinary(path: String, bytes: ByteArray) = withContext(dispatchers.io) {
        AppLog.v(TAG, "writeBinary-to: $path")
        // the first chunk creates the file, the rest are appended
        var offset = 0
        do {
            val chunk = bytes.copyOfRange(offset, minOf(offset + CHUNK_SIZE, bytes.size))
            val code = CommandsManager.writeBinaryFile(path, chunk, append = offset > 0)
            val result = replManager.writeInSilentMode(code)
            AppLog.i(TAG, "result $result")
            offset += CHUNK_SIZE
        } while (offset < bytes.size)
    }

    /** Publishes the listing in [json] to [files], keeping the current one if it can't be read. */
    private fun updateFiles(json: String) {
        decodeFiles(json)?.let { _files.value = it }
    }

    /**
     * Decodes the JSON response from the board manager into a list of `MicroFile` objects.
     *
     * This method parses the JSON response received from the MicroPython board
     * and creates a list of `MicroFile` objects representing the files and
     * directories in the current working directory. objects are sorted to show directories
     * first then files.
     *
     * @param json The JSON response string received from the board manager.
     * @return the files, or null when [json] is not a listing.
     */
    private fun decodeFiles(json: String): List<MicroFile>? {
        val list = mutableListOf<MicroFile>()
        val jsonFormated = json.replace("(", "[").replace(")", "]").replace("'", "\"")
        val items: JsonArray?
        try {
            items = Json.parseToJsonElement(jsonFormated).jsonArray
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        for (i in items.indices) {
            val item = items[i] as? JsonArray ?: continue
            val length = item.size

            if (length >= 3) {
                val name = (item[0] as? JsonPrimitive)?.content.orEmpty()
                val type = (item[1] as? JsonPrimitive)?.intOrNull ?: 0x8000
                val size = if (length == 4) ((item[3] as? JsonPrimitive)?.intOrNull ?: 0)
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
        AppLog.i(TAG, sortedFiles.toString())
        return sortedFiles
    }
}
