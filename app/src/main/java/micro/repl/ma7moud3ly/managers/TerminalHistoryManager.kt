/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log

class TerminalHistoryManager {
    companion object {
        private const val TAG = "TerminalHistory"
    }

    private var historyIndex = 0
    private val history = mutableListOf<String>()

    fun push(value: String) {
        Log.i(TAG, "push")
        if (history.contains(value).not()) {
            history.add(value)
            historyIndex = history.size - 1
        }
    }

    fun up(): String? {
        Log.i(TAG, "up----> ${history.size} | $historyIndex")
        return if (history.isNotEmpty() && historyIndex >= 0) history[historyIndex--] else null
    }

    fun down(): String? {
        Log.i(TAG, "down----> ${history.size} | $historyIndex")
        if (historyIndex == -1) historyIndex = 0
        if (historyIndex + 1 < history.size) historyIndex++
        return if (history.isNotEmpty()) history[historyIndex] else null
    }
}