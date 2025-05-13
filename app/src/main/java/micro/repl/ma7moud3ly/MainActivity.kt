/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.managers.isDark
import micro.repl.ma7moud3ly.screens.RootGraph
import micro.repl.ma7moud3ly.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var boardManager: BoardManager
    private lateinit var terminalManager: TerminalManager
    private lateinit var filesManager: FilesManager
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardManager = BoardManager(
            context = this,
            onStatusChanges = { viewModel.status.value = it },
            onReceiveData = { data: String, clear: Boolean ->
                runOnUiThread {
                    if (clear) viewModel.terminalOutput.value = ""
                    // limit terminal output to 10000 chars to avoid app
                    // freeze for very large outputs
                    else if (viewModel.terminalOutput.value.length > 10000)
                        viewModel.terminalOutput.value = data
                    else viewModel.terminalOutput.value += data
                }
            }
        )
        terminalManager = TerminalManager(boardManager)
        filesManager = FilesManager(
            boardManager = boardManager,
            onUpdateFiles = { viewModel.files.value = it }
        )
        setContent {
            AppTheme(
                darkTheme = this.isDark(),
                darkStatusBar = true
            ) {
                RootGraph(
                    viewModel = viewModel,
                    boardManager = boardManager,
                    terminalManager = terminalManager,
                    filesManager = filesManager
                )
            }
        }
    }
}


