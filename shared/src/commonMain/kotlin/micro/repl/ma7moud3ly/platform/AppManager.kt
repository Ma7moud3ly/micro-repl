/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import androidx.compose.runtime.Composable
import micro.repl.ma7moud3ly.managers.port.AppManager

/**
 * The [AppManager] for the window this composition is running in.
 *
 * Returns a new one whenever that window changes shape, so [AppManager.isPortrait]
 * always reports the current orientation.
 */
@Composable
expect fun rememberAppManager(): AppManager
