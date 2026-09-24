/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ma7moud3ly.nemo.model.EditorTheme
import micro.repl.ma7moud3ly.platform.StorageManager
import micro.repl.ma7moud3ly.ui.theme.AppThemes
import org.koin.core.annotation.Single

/**
 * The theme the app is wearing. One [EditorTheme] paints both the code editor and
 * the rest of the app.
 *
 */
@Single
class ThemesManager(private val storageManager: StorageManager) {

    /** The active theme. */
    var theme by mutableStateOf(restore())
        private set

    /** All themes offered by the picker. */
    val themes: List<EditorTheme> get() = AppThemes.ALL

    fun select(theme: EditorTheme) {
        this.theme = theme
        storageManager.themeName = theme.name
    }

    /**
     * Resolves the persisted theme name back to an [EditorTheme].
     *
     * With nothing persisted the app starts on [AppThemes.DEFAULT].
     */
    private fun restore(): EditorTheme =
        AppThemes.ALL.firstOrNull { it.name == storageManager.themeName }
            ?: AppThemes.DEFAULT
}
