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
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = secondaryColorDark,
    onSecondary = Color.White,
    tertiary = blue,
    onTertiary = Color.Black,
    outlineVariant = dividerColor,
    surface = surfaceColorDark,
    background = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = secondaryColor,
    onSecondary = Color.Black,
    tertiary = blue,
    onTertiary = Color.White,
    outlineVariant = dividerColor,
    surface = surfaceColor,
    background = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkStatusBar: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /* val colorScheme = when {
         dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
             val context = LocalContext.current
             if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
         }
         darkTheme -> DarkColorScheme
         else -> LightColorScheme
     }*/
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    ConfigureStatusBar(view, darkStatusBar || darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun ConfigureStatusBar(
    view: View,
    darkTheme: Boolean
) {
    if (!view.isInEditMode) {
        val activity = view.context as Activity
        SideEffect {
            activity.window.statusBarColor =
                if (darkTheme) Color.Black.toArgb() else Color.White.toArgb()
            WindowCompat.getInsetsController(
                activity.window, view
            ).isAppearanceLightStatusBars = darkTheme.not()
        }
    }
}