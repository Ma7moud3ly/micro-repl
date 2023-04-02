package com.ma7moud3ly.microterminal.util

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray


class FileManager {
    val files = mutableStateListOf<MicroFile>()

    init {
        val json = "[('main.py', 0x8000, 0, 268), ('man', 0x4000, 0, 0), ('well', 0x4000, 0, 4)]"
        listDir(json)
        Log.i(TAG, "files ---> ${files.toList()}")
    }

    fun listDir(json: String) {
        val list = mutableListOf<MicroFile>()
        val jsonFormated = json.replace("(", "[").replace(")", "]")
        val items = JSONArray(jsonFormated)
        for (i in 0 until items.length()) {
            val item = items[i] as? JSONArray
            if (item?.length() == 4) {
                val name = item[0] as? String ?: ""
                val type = item[1] as? Int ?: 0x8000
                val size = item[3] as? Int ?: 0
                list.add(MicroFile(name = name, type = type, size = size))
            }
        }
        files.clear()
        files.addAll(list)
    }

    companion object {
        private const val TAG = "FileManager"
    }
}

//[('main.py', 32768, 0, 268), ('man', 16384, 0, 0), ('well', 32768, 0, 4)]
data class MicroFile(
    val name: String,
    private val type: Int,
    private val size: Int,
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