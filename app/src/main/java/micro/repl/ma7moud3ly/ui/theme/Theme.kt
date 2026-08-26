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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.ma7moud3ly.nemo.model.EditorTheme


/**
 * Maps a Nemo [EditorTheme] onto a Material [ColorScheme] so the whole app is
 * painted with the same palette as the code editor.
 *
 * Roles are derived from the editor's own parts:
 * - `background`  <- editor background, `surface` <- gutter (a distinct bar layer)
 * - `surfaceVariant` <- current-line highlight (selected fills)
 * - `outline` / `outlineVariant` <- selection / current-line (borders + hairlines)
 * - `primary` <- **foreground**, because the app uses `primary` as a content
 *   colour for text and icons, not as an accent fill.
 *
 * Unmapped roles fall back to the Material baseline via `copy`.
 */
fun EditorTheme.toColorScheme(): ColorScheme {
    val background = background.toColor()
    val foreground = foreground.toColor()
    val gutter = gutter.toColor()
    val currentLine = currentLineBackground.toColor()
    val selection = selection.toColor()
    val lineNumber = lineNumber.toColor()
    val accent = lineNumberActive.toColor()

    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = foreground,
        onPrimary = background,
        secondary = currentLine,
        onSecondary = foreground,
        tertiary = accent,
        onTertiary = background,
        background = background,
        onBackground = foreground,
        surface = gutter,
        onSurface = foreground,
        surfaceVariant = currentLine,
        onSurfaceVariant = lineNumber,
        outline = selection,
        // hairlines sit halfway between the surface and a full outline
        outlineVariant = lerp(gutter, selection, 0.5f),
        inverseSurface = foreground,
        inverseOnSurface = gutter,
        surfaceTint = accent,
        error = syntax.type.toColor(),
        onError = background
    )
}


/**
 * Themes the app with a Nemo [EditorTheme].
 */
@Composable
fun AppTheme(
    theme: EditorTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(theme) { theme.toColorScheme() }
    val statusColors = remember(theme) { theme.toStatusColors() }

    ConfigureSystemBars(
        view = LocalView.current,
        darkTheme = theme.dark,
        statusBarColor = colorScheme.surface,
        navigationBarColor = colorScheme.background
    )

    CompositionLocalProvider(LocalStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/** Light/dark convenience overload — keeps `@Preview`s simple. */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = AppTheme(
    theme = if (darkTheme) AppThemes.DEFAULT_DARK else AppThemes.DEFAULT_LIGHT,
    content = content
)

@Composable
private fun ConfigureSystemBars(
    view: View,
    darkTheme: Boolean,
    statusBarColor: Color,
    navigationBarColor: Color
) {
    if (view.isInEditMode) return
    val activity = view.context as? Activity ?: return
    SideEffect {
        @Suppress("DEPRECATION")
        activity.window.statusBarColor = statusBarColor.toArgb()
        @Suppress("DEPRECATION")
        activity.window.navigationBarColor = navigationBarColor.toArgb()
        WindowCompat.getInsetsController(activity.window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
