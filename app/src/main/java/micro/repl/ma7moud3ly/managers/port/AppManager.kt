/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers.port

/**
 * The things only the platform host can do to the app's window.
 *
 * Android answers through the Activity. Desktop reports the window shape and
 * relaunches the window; web reads the viewport and reloads the page. Where a
 * platform owns the decision itself, the setters are no-ops.
 */
interface AppManager {

    /** Whether the window is currently taller than it is wide. */
    val isPortrait: Boolean

    /** Flips portrait to landscape and back. */
    fun toggleOrientation()

    /** Pins the screen to portrait. */
    fun forcePortrait()

    /** Rebuilds the UI from scratch, as after a theme or locale change. */
    fun restart()
}
