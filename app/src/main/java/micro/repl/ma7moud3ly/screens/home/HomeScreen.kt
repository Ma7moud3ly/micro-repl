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
import micro.repl.ma7moud3ly.managers.isDark
import micro.repl.ma7moud3ly.managers.isPortrait
import micro.repl.ma7moud3ly.managers.toggleOrientationMode
import micro.repl.ma7moud3ly.managers.toggleThemeMode
import micro.repl.ma7moud3ly.model.MicroDevice

private const val TAG = "HomeScreen"

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
    val isDark = remember { activity.isDark() }
    val isPortrait = remember { activity.isPortrait() }

    fun onApproveDevice(microDevice: MicroDevice) {
        Log.i(TAG, "onApproveDevice")
        boardManager?.approveDevice(microDevice.usbDevice!!)
    }

    fun onForgetDevice(microDevice: MicroDevice) {
        Log.i(TAG, "onForgetDevice")
        boardManager?.onForgetDevice(microDevice.usbDevice!!)
    }

    fun onDenyDevice() {
        Log.i(TAG, "onDenyDevice")
        boardManager?.onDenyDevice()
    }

    fun onDisconnectDevice() {
        Log.i(TAG, "onDisconnectDevice")
        boardManager?.onDisconnectDevice()
    }

    fun onFindDevices() {
        Log.i(TAG, "onFindDevices")
        boardManager?.detectUsbDevices()
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
        terminalManager?.terminateExecution {
            Toast.makeText(
                activity,
                activity.getString(R.string.terminal_terminate_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val status = viewModel.status.collectAsState()
    HomeScreenContent(
        isDark = isDark,
        isPortrait = isPortrait,
        connectionStatus = { status.value },
        uiEvents = {
            when (it) {
                is HomeEvents.ApproveDevice -> onApproveDevice(it.microDevice)
                is HomeEvents.ForgetDevice -> onForgetDevice(it.microDevice)
                is HomeEvents.DenyDevice -> onDenyDevice()
                is HomeEvents.DisconnectDevice -> onDisconnectDevice()
                is HomeEvents.FindDevices -> onFindDevices()
                is HomeEvents.Help -> onHelp()
                is HomeEvents.Reset -> onReset()
                is HomeEvents.SoftReset -> onSoftReset()
                is HomeEvents.Terminate -> onTerminate()
                is HomeEvents.OpenEditor -> openEditor()
                is HomeEvents.OpenExplorer -> openExplorer()
                is HomeEvents.OpenScripts -> openScripts()
                is HomeEvents.OpenTerminal -> openTerminal()
                is HomeEvents.ToggleMode -> {
                    activity.toggleThemeMode()
                }

                is HomeEvents.ToggleOrientation -> {
                    activity.toggleOrientationMode()
                }
            }
        }
    )
}