/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.HomeCommand
import micro.repl.ma7moud3ly.model.asSuccessMessage
import micro.repl.ma7moud3ly.platform.rememberAppManager
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.components.rememberMessageState
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Home screen: connects a board, reports connection state, and opens the
 * terminal / explorer / editor / scripts. Themed with [AppTheme].
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    openThemePicker: () -> Unit,
    openTerminal: () -> Unit,
    openEditor: () -> Unit,
    openScripts: () -> Unit,
    openExplorer: () -> Unit
) {
    val appManager = rememberAppManager()
    val status = viewModel.status.collectAsStateWithLifecycle()
    val messageToast = rememberMessageState()
    val context= LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.commands.collect { command ->
            val text = when (command) {
                HomeCommand.DeviceReset -> R.string.terminal_reset_msg
                HomeCommand.DeviceSoftReset -> R.string.terminal_soft_reset_msg
                HomeCommand.ExecutionTerminated -> R.string.terminal_terminate_msg
            }
            messageToast.show(context.getString(text).asSuccessMessage)
        }
    }


    MessageToast(state = messageToast)

    HomeScreenContent(
        isPortrait = appManager.isPortrait,
        connectionStatus = { status.value },
        uiEvents = {
            when (it) {
                is HomeEvents.OpenTerminal -> {
                    viewModel.newScript()
                    openTerminal()
                }
                is HomeEvents.OpenEditor -> {
                    viewModel.newScript()
                    openEditor()
                }
                is HomeEvents.OpenExplorer -> openExplorer()
                is HomeEvents.OpenScripts -> openScripts()
                is HomeEvents.Reset -> viewModel.reset()
                is HomeEvents.SoftReset -> viewModel.softReset()
                is HomeEvents.Terminate -> viewModel.terminate()
                is HomeEvents.Connect -> viewModel.connect()
                is HomeEvents.Disconnect -> viewModel.disconnect()
                is HomeEvents.DenyDevice -> viewModel.denyDevice()
                is HomeEvents.ApproveDevice -> viewModel.approveDevice(it.microDevice)
                is HomeEvents.ForgetDevice -> viewModel.forgetDevice(it.microDevice)
                is HomeEvents.ShowThemeDialog -> openThemePicker()
                is HomeEvents.RestartApp -> appManager.restart()
                is HomeEvents.ToggleOrientation -> appManager.toggleOrientation()
            }
        }
    )
}
