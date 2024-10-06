package micro.repl.ma7moud3ly.screens.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.TerminalManager
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

    val context = LocalContext.current

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
                Uri.parse(context.getString(R.string.home_help_link))
            )
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onReset() {
        viewModel.microDevice?.let {
            terminalManager?.resetDevice(it) {
                Toast.makeText(
                    context,
                    context.getString(R.string.terminal_reset_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onSoftReset() {
        viewModel.microDevice?.let {
            terminalManager?.softResetDevice {
                Toast.makeText(
                    context,
                    context.getString(R.string.terminal_soft_reset_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onTerminate() {
        terminalManager?.terminateExecution {
            Toast.makeText(
                context,
                context.getString(R.string.terminal_terminate_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val status = viewModel.status.collectAsState()
    HomeScreenContent(
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
            }
        }
    )
}