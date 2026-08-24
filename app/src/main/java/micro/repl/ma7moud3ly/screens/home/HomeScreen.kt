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
import androidx.core.net.toUri
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.managers.isPortrait
import micro.repl.ma7moud3ly.managers.toggleOrientationMode
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.ui.theme.LocalThemeController
import micro.repl.ma7moud3ly.ui.theme.NewHomeTheme

private const val TAG = "HomeScreen"

/**
 * Home screen: connects a board, reports connection state, and opens the
 * terminal / explorer / editor / scripts. Themed with [NewHomeTheme].
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    boardManager: BoardManager?,
    terminalManager: TerminalManager?,
    openTerminal: () -> Unit,
    openEditor: () -> Unit,
    openScripts: () -> Unit,
    openExplorer: () -> Unit
) {
    val activity = LocalActivity.current as Activity
    val themeController = LocalThemeController.current
    val isDark = themeController.isDark
    val isPortrait = remember { activity.isPortrait() }

    fun onApproveDevice(microDevice: MicroDevice) {
        boardManager?.approveDevice(microDevice.usbDevice!!)
    }

    fun onForgetDevice(microDevice: MicroDevice) {
        boardManager?.onForgetDevice(microDevice.usbDevice!!)
    }

    fun onReset() {
        viewModel.microDevice?.let {
            terminalManager?.resetDevice(it) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.terminal_reset_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onSoftReset() {
        viewModel.microDevice?.let {
            terminalManager?.softResetDevice {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.terminal_soft_reset_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onTerminate() {
        terminalManager?.terminateExecution()
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
    NewHomeTheme(darkTheme = isDark) {
        HomeScreenContent(
            isDark = isDark,
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
                    is HomeEvents.Connect -> boardManager?.detectUsbDevices()
                    is HomeEvents.Disconnect -> boardManager?.onDisconnectDevice()
                    is HomeEvents.RestartApp -> activity.recreate()
                    is HomeEvents.ToggleTheme -> themeController.toggle()
                    is HomeEvents.ToggleOrientation -> activity.toggleOrientationMode()
                    is HomeEvents.Help -> onHelp()
                    is HomeEvents.DenyDevice -> boardManager?.onDenyDevice()
                    is HomeEvents.ApproveDevice -> onApproveDevice(it.microDevice)
                    is HomeEvents.ForgetDevice -> onForgetDevice(it.microDevice)
                }
            }
        )
    }
}
