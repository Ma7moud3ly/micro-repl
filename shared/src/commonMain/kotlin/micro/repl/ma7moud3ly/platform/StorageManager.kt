/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

/**
 * The small pieces of state the app remembers between launches.
 *
 * Shared preferences on Android, a config file or the registry elsewhere - callers
 * only see typed values, never keys or a storage handle.
 */
interface StorageManager {

    ////// Boards

    /** Product ids the user has already approved, or an empty set on first run. */
    fun approvedProductIds(): MutableSet<Int>

    /** Called after a board is approved or forgotten. */
    fun saveApprovedProductIds(productIds: Set<Int>)

    ////// Theme

    /** Name of the selected theme, empty until one has been picked. */
    var themeName: String

    ////// Editor

    /** Editor font size. Clamped to the 8..32 the editor accepts. */
    var fontSize: Int

    /** Whether the editor gutter shows line numbers. */
    var showLineNumbers: Boolean

    /** Path of the last local script that was open, reopened on a blank launch. */
    var recentScript: String
}
