/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.util.Log

/**
 * Manages the command history for a terminal or REPL interface.
 *
 * This class provides functionality for storing, retrieving, and navigating
 * through the history of commands entered by the user. It maintains a list
 * of commands and allows the user to move up and down through the history
 * using the `up()` and `down()` methods.
 */
class TerminalHistoryManager {
    companion object {
        private const val TAG = "TerminalHistory"
    }

    /**
     * The current index within the command history.
     */
    private var historyIndex = 0

    /**
     * The list of commands stored in the history.
     */
    private val history = mutableListOf<String>()

    /**
     * Adds a new command to the history.
     *
     * If the command is not already in the history, it is added to the end
     * of the list and the history index is updated to point to the new command.
     *
     * @param value The command to add to the history.
     */
    fun push(value: String) {
        Log.i(TAG, "push")
        if (history.contains(value).not()) {
            history.add(value)
            historyIndex = history.size - 1
        }
    }

    /**
     * Retrieves the previous command in the history.
     *
     * Moves the history index one step back and returns the command at that
     * position. If the history index is already at the beginning of the history,
     * returns `null`.
     *
     * @return The previous command in the history, or `null` if there are no
     *         previous commands.
     */
    fun up(): String? {
        Log.i(TAG, "up----> ${history.size} | $historyIndex")
        return if (history.isNotEmpty() && historyIndex >= 0) history[historyIndex--] else null
    }

    /**
     * Retrieves the next command in the history.
     *
     * Moves the history index one step forward and returns the command at that
     * position. If the history index is already at the end of the history,
     * returns `null`.
     *
     * @return The next command in the history, or `null` if there are no
     *         next commands.
     */
    fun down(): String? {
        Log.i(TAG, "down----> ${history.size} | $historyIndex")
        if (historyIndex == -1) historyIndex = 0
        if (historyIndex + 1 < history.size) historyIndex++
        return if (history.isNotEmpty()) history[historyIndex] else null
    }
}