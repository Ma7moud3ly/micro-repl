/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo

/**
 * Reports the desktop window's shape. The orientation setters and [WindowManager.restart]
 * do nothing.
 */
@Composable
actual fun rememberWindowManager(): WindowManager {
    val size = LocalWindowInfo.current.containerSize
    return remember(size) {
        object : WindowManager {
            override val isPortrait: Boolean = size.height >= size.width
            override fun setSystemBars(
                statusBar: Color,
                navigationBar: Color,
                darkIcons: Boolean
            ) = Unit

            override fun toggleOrientation() = Unit
            override fun forcePortrait() = Unit
            override fun restart() = Unit
        }
    }
}
