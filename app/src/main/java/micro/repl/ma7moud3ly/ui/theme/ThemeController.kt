/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.content.edit
import io.ma7moud3ly.nemo.model.EditorTheme

private const val KEY_THEME = "editor_theme"
private const val KEY_DARK_MODE = "dark_mode"

/**
 * Compose-managed theme state. The selected Nemo [EditorTheme] paints both the
 * code editor and the rest of the app (see [AppTheme]).
 *
 * Changing [theme] triggers recomposition of the themed subtree — no Activity
 * restart. Restarting is expensive here because it tears down the USB connection,
 * so theme changes are done purely in Compose and only persisted to preferences.
 */
class ThemeController(
    initialTheme: EditorTheme,
    private val persist: (EditorTheme) -> Unit = {}
) {
    var theme by mutableStateOf(initialTheme)
        private set

    /** Whether the active theme is a dark one. */
    val isDark: Boolean get() = theme.dark

    /** All themes offered by the picker. */
    val themes: List<EditorTheme> get() = AppThemes.ALL

    fun select(theme: EditorTheme) {
        this.theme = theme
        persist(theme)
    }

    /** Jumps to the default light or dark theme. */
    fun toggle() {
        select(if (isDark) AppThemes.DEFAULT_LIGHT else AppThemes.DEFAULT_DARK)
    }
}

// Falls back to a non-persisting light controller when unprovided (e.g. @Preview),
// so reads never crash outside the root.
val LocalThemeController = staticCompositionLocalOf {
    ThemeController(AppThemes.DEFAULT)
}

@Composable
fun rememberThemeController(activity: Activity): ThemeController = remember {
    val prefs = activity.getPreferences(Context.MODE_PRIVATE)
    ThemeController(
        initialTheme = restoreTheme(prefs.getString(KEY_THEME, null), prefs.getBoolean(KEY_DARK_MODE, true)),
        persist = { theme ->
            prefs.edit {
                putString(KEY_THEME, theme.name)
                // kept in sync so anything still reading the old flag stays correct
                putBoolean(KEY_DARK_MODE, theme.dark)
            }
        }
    )
}

/**
 * Resolves a persisted theme name back to an [EditorTheme].
 *
 * With nothing persisted the app starts on [AppThemes.DEFAULT]. Users upgrading
 * from the old light/dark switch have no theme name yet, so their `dark_mode`
 * flag still decides — [dark] defaults to true, keeping fresh installs dark.
 */
private fun restoreTheme(name: String?, dark: Boolean): EditorTheme =
    AppThemes.ALL.firstOrNull { it.name == name }
        ?: if (dark) AppThemes.DEFAULT else AppThemes.DEFAULT_LIGHT
