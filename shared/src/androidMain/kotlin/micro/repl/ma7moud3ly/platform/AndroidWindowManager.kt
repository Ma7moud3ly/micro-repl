/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat

/**
 * Drives the hosting Activity: sets its requested orientation, and recreates it
 * on [WindowManager.restart].
 *
 * Rebuilt on every configuration change.
 */
@Composable
actual fun rememberWindowManager(): WindowManager {
    val activity = LocalActivity.current
    val orientation = LocalConfiguration.current.orientation
    return remember(activity, orientation) {
        AndroidWindowManager(
            activity = activity,
            isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        )
    }
}

@SuppressLint("SourceLockedOrientationActivity")
private class AndroidWindowManager(
    private val activity: Activity?,
    override val isPortrait: Boolean
) : WindowManager {

    override fun toggleOrientation() {
        activity?.requestedOrientation = if (isPortrait) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun forcePortrait() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun restart() {
        activity?.recreate()
    }

    override fun setSystemBars(statusBar: Color, navigationBar: Color, darkIcons: Boolean) {
        val window = activity?.window ?: return
        @Suppress("DEPRECATION")
        window.statusBarColor = statusBar.toArgb()
        @Suppress("DEPRECATION")
        window.navigationBarColor = navigationBar.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = darkIcons
            isAppearanceLightNavigationBars = darkIcons
        }
    }
}
