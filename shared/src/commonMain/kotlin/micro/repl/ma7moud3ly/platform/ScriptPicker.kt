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
     * when there is none. [MicroScript.name] is the name the dialog suggests.
     *
     * @return the script as written, its path and name pointing at the file, or
     * null if the dialog was dismissed or the file could not be written.
     */
    suspend fun save(script: MicroScript): MicroScript?

    /**
     * A bookmark for the file last opened or saved, keeping read and write
     * access to it across sessions.
     *
     * @return the bookmark, or null when there is no file or the platform can't
     * keep access.
     */
    suspend fun bookmark(): String?

    /**
     * Reads the file behind [bookmark], and remembers it like [open] does.
     *
     * @return the script, or null if the file is gone or can't be read.
     */
    suspend fun reopen(bookmark: String): MicroScript?

    /** Gives up the access [bookmark] kept. */
    fun forget(bookmark: String)
}
