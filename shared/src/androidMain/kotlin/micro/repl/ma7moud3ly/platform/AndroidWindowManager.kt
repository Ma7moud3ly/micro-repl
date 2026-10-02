/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

@Composable
actual fun rememberWindowManager(): WindowManager {
    val activity = LocalActivity.current
    return remember(activity) { AndroidWindowManager(activity) }
}

/**
 * Paints the Activity's system bars.
 */
private class AndroidWindowManager(
    private val activity: Activity?
) : WindowManager {


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
