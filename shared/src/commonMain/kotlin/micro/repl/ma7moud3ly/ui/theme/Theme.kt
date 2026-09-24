/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.lerp
import io.ma7moud3ly.nemo.model.EditorTheme
import micro.repl.ma7moud3ly.platform.rememberWindowManager


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


val LocalEditorTheme = staticCompositionLocalOf { AppThemes.DEFAULT }

@Composable
fun AppTheme(
    theme: EditorTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(theme) { theme.toColorScheme() }
    val statusColors = remember(theme) { theme.toStatusColors() }
    val windowManager = rememberWindowManager()

    LaunchedEffect(windowManager, theme) {
        windowManager.setSystemBars(
            statusBar = colorScheme.surface,
            navigationBar = colorScheme.background,
            darkIcons = theme.dark.not()
        )
    }

    CompositionLocalProvider(LocalStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography,
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
