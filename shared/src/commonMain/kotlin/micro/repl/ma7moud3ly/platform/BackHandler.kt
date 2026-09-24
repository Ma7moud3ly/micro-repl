/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import androidx.compose.runtime.Composable

/**
 * Runs [onBack] when the platform's back gesture fires while [enabled].
 *
 * Only Android has one; elsewhere this does nothing.
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
