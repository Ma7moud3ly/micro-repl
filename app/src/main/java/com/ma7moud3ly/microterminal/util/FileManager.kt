package com.ma7moud3ly.microterminal.util

import android.util.Log
import org.json.JSONArray


class FileManager(
    private val usbManager: UsbManager,
    private val onUpdateFiles: ((files: List<MicroFile>) -> Unit)? = null
) {
    fun listDir() {
        val code = CommandsManager.iListDir("")
        usbManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "response $result")
            decodeFiles(result)
        })
    }

    fun remove(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.removeFile(file)
        else CommandsManager.removeDirectory(file)
        usbManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "response $result")
            decodeFiles(result)
        })
    }

    fun new(file: MicroFile) {
        val code = if (file.isFile) CommandsManager.makeFile(file)
        else CommandsManager.makeDirectory(file)
        usbManager.writeSync(code, onResponse = { response ->
            val result = CommandsManager.extractResult(response, default = "[]")
            Log.i(TAG, "response $response")
            Log.i(TAG, "response $result")
            decodeFiles(result)
        })
    }

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
                list.add(MicroFile(name = name, type = type, size = size))
            }
        }
        Log.i(TAG, list.toString())
        onUpdateFiles?.invoke(list)
    }

    companion object {
        private const val TAG = "FileManager"
    }
}

data class MicroFile(
    val name: String,
    val path: String = "",
    private val type: Int = FILE,
    private val size: Int = 0,
) {
    val isFile: Boolean get() = type == FILE
    val canRun: Boolean get() = ext == ".py"

    private val ext: String
        get() {
            return if (isFile && name.contains(".")) name.substring(name.indexOf(".")).trim()
            else ""
        }

    companion object {
        const val DIRECTORY = 0x4000
        const val FILE = 0x8000
    }
}