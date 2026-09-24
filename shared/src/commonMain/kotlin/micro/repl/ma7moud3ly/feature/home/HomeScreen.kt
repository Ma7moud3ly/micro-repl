/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import micro.repl.ma7moud3ly.platform.rememberWindowManager
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.terminal_reset_msg
import micro.repl.ma7moud3ly.shared.resources.terminal_soft_reset_msg
import micro.repl.ma7moud3ly.shared.resources.terminal_terminate_msg
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.components.asSuccessMessage
import micro.repl.ma7moud3ly.ui.components.rememberMessageState
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

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
    val windowManager = rememberWindowManager()
    val status = viewModel.status.collectAsStateWithLifecycle()
    val messageToast = rememberMessageState()

    LaunchedEffect(Unit) {
        viewModel.commands.collect { command ->
            val text = getString(
                when (command) {
                    HomeCommand.DeviceReset -> Res.string.terminal_reset_msg
                    HomeCommand.DeviceSoftReset -> Res.string.terminal_soft_reset_msg
                    HomeCommand.ExecutionTerminated -> Res.string.terminal_terminate_msg
                }
            )
            messageToast.show(text.asSuccessMessage)
        }
    }


    MessageToast(state = messageToast)

    HomeScreenContent(
        isPortrait = windowManager.isPortrait,
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
                is HomeEvents.RestartApp -> windowManager.restart()
                is HomeEvents.ToggleOrientation -> windowManager.toggleOrientation()
            }
        }
    )
}
