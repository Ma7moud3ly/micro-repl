package micro.repl.ma7moud3ly.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import io.ma7moud3ly.nemo.model.EditorTheme


internal fun Long.toColor() = Color(this)


/**
 * Connection-status accents plus the two neutral roles Material has no slot for
 * ([muted] = onSurfaceMuted, [selected] = segment/badge selected fill).
 *
 * Values are derived from the active editor theme — see `EditorTheme.toStatusColors()`.
 */
data class StatusColors(
    val ok: Color,
    val warn: Color,
    val error: Color,
    val muted: Color,
    val selected: Color
)


/**
 * Connection-status accents pulled from the theme's syntax palette, so they
 * stay legible on every theme instead of being fixed reds/greens.
 */
fun EditorTheme.toStatusColors() = StatusColors(
    ok = syntax.string.toColor(),
    warn = syntax.number.toColor(),
    error = syntax.type.toColor(),
    // comments are the theme's own "muted text" role
    muted = syntax.comment.toColor(),
    selected = currentLineBackground.toColor()
)


/**
 * Read the current status accents: `LocalStatusColors.current`.
 * Falls back to the default theme's palette so reads outside [AppTheme]
 * (e.g. `@Preview`) still resolve.
 */
val LocalStatusColors = staticCompositionLocalOf { AppThemes.DEFAULT.toStatusColors() }

data class ExplorerColors(
    val file: Color,
    val folder: Color,
    val new: Color,
)

val explorerColors = ExplorerColors(
    folder = Color(0xFFFFE69A),
    file = Color(0xFFE8E8E8),
    new = Color(0xFFE8E8E8)
)
