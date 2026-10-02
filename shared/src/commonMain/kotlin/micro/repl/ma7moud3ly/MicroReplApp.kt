/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import micro.repl.ma7moud3ly.platform.LocalPlatform
import micro.repl.ma7moud3ly.platform.currentPlatform
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.LocalEditorTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MicroReplApp(
    viewModel: MainViewModel = koinViewModel(),
    topBar: @Composable () -> Unit = {}
) {
    CompositionLocalProvider(
        LocalEditorTheme provides viewModel.theme,
        LocalPlatform provides currentPlatform
    ) {
        AppTheme(theme = viewModel.theme) {
            Column(Modifier.fillMaxSize()) {
                topBar()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    RootGraph(viewModel = viewModel)
                }
            }
        }
    }
}
