/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import micro.repl.ma7moud3ly.model.MicroScript

/** Script types the picker offers. */
internal val ScriptTypes = setOf("py", "txt", "json")

internal const val TAG = "ScriptPicker"

/**
 * Opens and saves a script the user chooses through the platform's own file
 * dialogs.
 *
 * The chosen file is remembered until the next [open], so [save] writes back to
 * it without a path.
 */
interface ScriptPicker {

    /**
     * Shows the file picker and reads what was chosen.
     *
     * @return the script with its content filled in, or null if the picker was
     * dismissed or the file could not be read.
     */
    suspend fun open(): MicroScript?

    /**
     * Writes [script] back to the file it was opened from, asking for a location
     * when there is none.
     *
     * @return false if the file could not be written, or the dialog was dismissed.
     */
    suspend fun save(script: MicroScript): Boolean
}
