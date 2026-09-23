/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.ma7moud3ly.nemo.model.EditorTheme
import micro.repl.ma7moud3ly.managers.ThemesManager
import micro.repl.ma7moud3ly.ui.theme.toColorScheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val themesManager: ThemesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfigureSystemBars(themesManager.theme)
            MicroReplApp()
        }
    }

    /**
     * Paints this window's status and navigation bars to match [theme].
     *
     * Window chrome is Android's alone, so it stays with the Activity rather than
     * in the shared theme - other platforms have nothing to configure.
     */
    @Composable
    private fun ConfigureSystemBars(theme: EditorTheme) {
        val view = LocalView.current
        if (view.isInEditMode) return
        val colorScheme = remember(theme) { theme.toColorScheme() }
        SideEffect {
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.surface.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = theme.dark.not()
                isAppearanceLightNavigationBars = theme.dark.not()
            }
        }
    }
}
