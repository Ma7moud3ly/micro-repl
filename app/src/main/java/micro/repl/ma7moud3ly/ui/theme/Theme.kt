/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.theme

import android.app.Activity
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NewHomeDarkScheme else NewHomeLightScheme
    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors

    ConfigureNewHomeStatusBar(LocalView.current, darkTheme, colorScheme.background)

    CompositionLocalProvider(LocalStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
private fun ConfigureNewHomeStatusBar(
    view: View,
    darkTheme: Boolean,
    background: Color
) {
    if (!view.isInEditMode) {
        val activity = view.context as Activity
        SideEffect {
            @Suppress("DEPRECATION")
            activity.window.statusBarColor = background.toArgb()
            WindowCompat
                .getInsetsController(activity.window, view)
                .isAppearanceLightStatusBars = darkTheme.not()
        }
    }
}
