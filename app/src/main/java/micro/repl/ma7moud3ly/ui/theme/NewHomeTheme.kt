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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val NewHomeDarkScheme = darkColorScheme(
    primary = Color(0xFFE8EAED),
    onPrimary = Color(0xFF14161A),
    background = Color(0xFF14161A),
    surface = Color(0xFF1B1D22),
    surfaceVariant = Color(0xFF202329),
    outline = Color(0xFF3A3F47),
    outlineVariant = Color(0xFF2A2E34),
    onSurface = Color(0xFFE8EAED),
    onSurfaceVariant = Color(0xFF9BA2A9),
    error = Color(0xFFEC7C63),
    inverseSurface = Color(0xFFE8EAED),
    inverseOnSurface = Color(0xFF1B1D22)
)

private val NewHomeLightScheme = lightColorScheme(
    primary = Color(0xFF17181A),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFFBFBFA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F0ED),
    outline = Color(0xFFDCDBD7),
    outlineVariant = Color(0xFFE8E7E4),
    onSurface = Color(0xFF17181A),
    onSurfaceVariant = Color(0xFF6B6F76),
    error = Color(0xFFB3402A),
    inverseSurface = Color(0xFF17181A),
    inverseOnSurface = Color(0xFFFFFFFF)
)

/**
 * Connection-status accents plus the two neutral roles Material has no slot for
 * ([muted] = onSurfaceMuted, [selected] = segment/badge selected fill).
 */
data class StatusColors(
    val ok: Color,
    val warn: Color,
    val error: Color,
    val muted: Color,
    val selected: Color
)

private val DarkStatusColors = StatusColors(
    ok = Color(0xFF3ADB8B),
    warn = Color(0xFFE8A93B),
    error = Color(0xFFEC7C63),
    muted = Color(0xFF6A7079),
    selected = Color(0xFF2A2F36)
)

private val LightStatusColors = StatusColors(
    ok = Color(0xFF12804E),
    warn = Color(0xFF9A6A0B),
    error = Color(0xFFB3402A),
    muted = Color(0xFF9AA0A6),
    selected = Color(0xFFF1F0ED)
)

/** Read the current status accents: `LocalStatusColors.current`. */
val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }


@Composable
fun NewHomeTheme(
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
