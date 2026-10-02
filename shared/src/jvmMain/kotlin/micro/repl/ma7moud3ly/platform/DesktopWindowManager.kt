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


@Composable
actual fun rememberWindowManager(): WindowManager {
    return remember {
        object : WindowManager {
            override fun setSystemBars(
                statusBar: Color,
                navigationBar: Color,
                darkIcons: Boolean
            ) = Unit

        }
    }
}
