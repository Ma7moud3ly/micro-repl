/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.core.content.edit

/**
 * Toggles the application's theme mode between light and dark.
 *
 * This method retrieves the current theme mode from SharedPreferences,
 * inverts it, and stores the new theme mode. It then recreates the
 * provided activity to apply the theme change.
 *
 */
fun Activity.toggleThemeMode() {
    this.getPreferences(Context.MODE_PRIVATE).edit {
        putBoolean("dark_mode", isDark().not())
    }
    this.recreate()
}

/**
 * Checks if the application is currently in dark mode.
 *
 * This method retrieves the theme mode from SharedPreferences. If the
 * "dark_mode" preference is not set, it defaults to `true` (dark mode).
 *
 * @return `true` if the application is in dark mode, `false` otherwise.
 */
fun Activity.isDark(): Boolean {
    val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
    return sharedPref.getBoolean("dark_mode", true)
}

/**
 * Toggles the orientation of the provided activity between portrait and landscape.
 *
 */
@SuppressLint("SourceLockedOrientationActivity")
fun Activity.toggleOrientationMode() {
    val currentOrientation = this.resources.configuration.orientation

    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
        // Currently in portrait, switch to landscape
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    } else {
        // Currently in landscape, switch to portrait
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

/**
 * Forces the activity's orientation to portrait.
 *
 */
@SuppressLint("SourceLockedOrientationActivity")
fun Activity.forcePortrait() {
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

/**
 * Checks if the current orientation of the activity is portrait.
 *
 * @return `true` if the activity's orientation is portrait, `false` otherwise.
 */
fun Activity.isPortrait(): Boolean {
    val currentOrientation = this.resources.configuration.orientation
    return currentOrientation == Configuration.ORIENTATION_PORTRAIT
}
