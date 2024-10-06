/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.managers.ThemeModeManager
import micro.repl.ma7moud3ly.model.ConnectionError
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.screens.RootGraph
import micro.repl.ma7moud3ly.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var boardManager: BoardManager
    private lateinit var terminalManager: TerminalManager
    private lateinit var filesManager: FilesManager
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPortraitOrientation()
        boardManager = BoardManager(
            context = this,
            onStatusChanges = {
                runOnUiThread {
                    viewModel.status.value = it
                    handleResponseMessage(it)
                }
            },
            onReceiveData = { data ->
                viewModel.terminalOutput.value += data
            }
        )
        terminalManager = TerminalManager(boardManager)
        filesManager = FilesManager(
            boardManager = boardManager,
            onUpdateFiles = { viewModel.files.value = it }
        )

        setContent {
            AppTheme(
                darkTheme = ThemeModeManager.isDark(this),
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


    private fun handleResponseMessage(status: ConnectionStatus) {
        val msg = when (status) {
            is ConnectionStatus.Connecting -> R.string.home_connecting
            is ConnectionStatus.Connected -> R.string.home_connected
            is ConnectionStatus.Approve -> return
            is ConnectionStatus.Error -> {
                when (status.error) {
                    ConnectionError.NO_DEVICES -> R.string.error_no_devices
                    ConnectionError.CONNECTION_LOST -> R.string.error_connection_lost
                    ConnectionError.CANT_OPEN_PORT -> R.string.error_cant_open_port
                    ConnectionError.PERMISSION_DENIED -> R.string.error_permission_denied
                    ConnectionError.NOT_SUPPORTED -> R.string.error_not_supported
                }
            }
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setPortraitOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

}


