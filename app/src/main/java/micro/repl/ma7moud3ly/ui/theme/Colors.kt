package micro.repl.ma7moud3ly.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val NewHomeDarkScheme = darkColorScheme(
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

internal val NewHomeLightScheme = lightColorScheme(
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

internal val DarkStatusColors = StatusColors(
    ok = Color(0xFF3ADB8B),
    warn = Color(0xFFE8A93B),
    error = Color(0xFFEC7C63),
    muted = Color(0xFF6A7079),
    selected = Color(0xFF2A2F36)
)

internal val LightStatusColors = StatusColors(
    ok = Color(0xFF12804E),
    warn = Color(0xFF9A6A0B),
    error = Color(0xFFB3402A),
    muted = Color(0xFF9AA0A6),
    selected = Color(0xFFF1F0ED)
)

/** Read the current status accents: `LocalStatusColors.current`. */
val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }

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
