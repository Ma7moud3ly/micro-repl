/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.LocalEditorTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MicroReplApp(viewModel: MainViewModel = koinViewModel()) {
    CompositionLocalProvider(LocalEditorTheme provides viewModel.theme) {
        AppTheme(theme = viewModel.theme) {
            RootGraph(viewModel = viewModel)
        }
    }
}
