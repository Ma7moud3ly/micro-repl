/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import micro.repl.ma7moud3ly.platform.LocalPlatform
import micro.repl.ma7moud3ly.platform.isMobile


/** How much larger text and controls are on a screen viewed from further away. */
internal const val DESKTOP_SCALE = 1.3f

/**
 * The size to draw a control at, grown off mobile by the same factor the theme
 * grows text by.
 */
val Dp.scaled: Dp
    @Composable get() =
        if (LocalPlatform.current.isMobile) this else this * DESKTOP_SCALE
