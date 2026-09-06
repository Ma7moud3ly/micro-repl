/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.isPortrait
import micro.repl.ma7moud3ly.managers.toggleOrientationMode
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.ui.theme.AppTheme

private const val TAG = "HomeScreen"

/**
 * Home screen: connects a board, reports connection state, and opens the
 * terminal / explorer / editor / scripts. Themed with [AppTheme].
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    openThemePicker: () -> Unit,
    openTerminal: () -> Unit,
    openEditor: () -> Unit,
    openScripts: () -> Unit,
    openExplorer: () -> Unit
) {
    val activity = LocalActivity.current as Activity
    val isPortrait = remember { activity.isPortrait() }
    val terminalManager = viewModel.terminalManager
    val coroutineScope = rememberCoroutineScope()

    fun onApproveDevice(microDevice: MicroDevice) {
        viewModel.approveDevice(microDevice)
    }

    fun onForgetDevice(microDevice: MicroDevice) {
        viewModel.onForgetDevice(microDevice)
    }

    fun onReset() {
        viewModel.microDevice?.let {
            coroutineScope.launch {
                terminalManager.resetDevice(it) {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.terminal_reset_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun onSoftReset() {
        viewModel.microDevice?.let {
            coroutineScope.launch {
                terminalManager.softResetDevice {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.terminal_soft_reset_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun onTerminate() {
        coroutineScope.launch { terminalManager.terminateExecution() }
        Toast.makeText(
            activity,
            activity.getString(R.string.terminal_terminate_msg),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onHelp() {
        try {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                activity.getString(R.string.home_help_link).toUri()
            )
            activity.startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val status = viewModel.status.collectAsState()

    HomeScreenContent(
        isPortrait = isPortrait,
        connectionStatus = { status.value },
        uiEvents = {
            Log.i(TAG, "event - $it")
            when (it) {
                is HomeEvents.OpenTerminal -> openTerminal()
                is HomeEvents.OpenExplorer -> openExplorer()
                is HomeEvents.OpenEditor -> openEditor()
                is HomeEvents.OpenScripts -> openScripts()
                is HomeEvents.Reset -> onReset()
                is HomeEvents.SoftReset -> onSoftReset()
                is HomeEvents.Terminate -> onTerminate()
                is HomeEvents.Connect -> viewModel.detectUsbDevices()
                is HomeEvents.Disconnect -> viewModel.boardManager.onDisconnectDevice()
                is HomeEvents.RestartApp -> activity.recreate()
                is HomeEvents.ShowThemeDialog -> openThemePicker()
                is HomeEvents.ToggleOrientation -> activity.toggleOrientationMode()
                is HomeEvents.Help -> onHelp()
                is HomeEvents.DenyDevice -> viewModel.boardManager.onDenyDevice()
                is HomeEvents.ApproveDevice -> onApproveDevice(it.microDevice)
                is HomeEvents.ForgetDevice -> onForgetDevice(it.microDevice)
            }
        }
    )
}
