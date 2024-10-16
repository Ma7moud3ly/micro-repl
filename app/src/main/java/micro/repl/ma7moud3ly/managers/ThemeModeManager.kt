/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.app.Activity
import android.content.Context

/**
 * Manages the application's theme mode (light or dark).
 *
 * This object provides methods for toggling the theme mode and checking
 * the current theme mode. The theme mode is persisted using SharedPreferences,
 * so it is retained across application sessions.
 */
object ThemeModeManager {

    /**
     * Toggles the application's theme mode between light and dark.
     *
     * This method retrieves the current theme mode from SharedPreferences,
     * inverts it, and stores the new theme mode. It then recreates the
     * provided activity to apply the theme change.
     *
     * @param activity The activity to recreate after changing the theme mode.
     */
    fun toggleMode(activity: Activity) {
        val sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPrefEditor.putBoolean("dark_mode", isDark(activity).not()).apply()
        activity.recreate()
    }

    /**
     * Checks if the application is currently in dark mode.
     *
     * This method retrieves the theme mode from SharedPreferences. If the
     * "dark_mode" preference is not set, it defaults to `true` (dark mode).
     *
     * @param activity The activity to get the SharedPreferences from.
     * @return `true` if the application is in dark mode, `false` otherwise.
     */
    fun isDark(activity: Activity): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean("dark_mode", true)
    }
}