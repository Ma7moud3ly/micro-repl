/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.LocalThemeController
import micro.repl.ma7moud3ly.ui.theme.rememberThemeController
import org.koin.androidx.compose.koinViewModel

@Composable
fun MicroReplApp(viewModel: MainViewModel = koinViewModel()) {
    val activity = LocalActivity.current!!

    LaunchedEffect(Unit) { viewModel.start() }

    val themeController = rememberThemeController(activity)
    CompositionLocalProvider(LocalThemeController provides themeController) {
        AppTheme(theme = themeController.theme) {
            RootGraph(viewModel = viewModel)
        }
    }
}
