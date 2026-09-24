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
import micro.repl.ma7moud3ly.managers.port.AppManager

/**
 * Reports the desktop window's shape. The orientation setters and [AppManager.restart]
 * do nothing.
 */
@Composable
actual fun rememberAppManager(): AppManager {
    val size = LocalWindowInfo.current.containerSize
    return remember(size) {
        object : AppManager {
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
