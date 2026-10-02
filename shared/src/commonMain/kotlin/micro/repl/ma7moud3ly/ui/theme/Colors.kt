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
 * Status accents: a fixed green, amber and red in a dark or light shade to
 * match the theme. [StatusColors.muted] and [StatusColors.selected] come from
 * the theme itself.
 */
fun EditorTheme.toStatusColors() = StatusColors(
    ok = if (dark) Color(0xFF3ADB8B) else Color(0xFF12804E),
    warn = if (dark) Color(0xFFE8A93B) else Color(0xFF9A6A0B),
    error = if (dark) Color(0xFFEC7C63) else Color(0xFFB3402A),
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
