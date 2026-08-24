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
import micro.repl.ma7moud3ly.managers.isDark

private const val KEY_DARK_MODE = "dark_mode"

/**
 * Compose-managed dark/light theme state.
 *
 * Flipping [isDark] triggers recomposition of the themed subtree — no Activity
 * restart. Restarting is expensive here because it tears down the USB connection,
 * so theme changes are done purely in Compose and only persisted to preferences.
 */
class ThemeController(
    initialDark: Boolean,
    private val persist: (Boolean) -> Unit
) {
    var isDark by mutableStateOf(initialDark)
        private set

    fun toggle() = setDarkMode(!isDark)

    fun setDarkMode(dark: Boolean) {
        isDark = dark
        persist(dark)
    }
}

val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    error("LocalThemeController not provided")
}

@Composable
fun rememberThemeController(activity: Activity): ThemeController = remember {
    ThemeController(
        initialDark = activity.isDark(),
        persist = { dark ->
            activity.getPreferences(Context.MODE_PRIVATE).edit {
                putBoolean(KEY_DARK_MODE, dark)
            }
        }
    )
}
