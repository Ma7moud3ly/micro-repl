/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.fragments

import android.content.Intent
import android.hardware.usb.UsbDevice
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.HomeScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.utils.EditorMode
import micro.repl.ma7moud3ly.utils.HomeUiEvents


class HomeFragment : BaseFragment(), HomeUiEvents {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = false, darkStatusBar = true) {
                    HomeScreen(viewModel, uiEvents = this@HomeFragment)
                }
            }
        }
    }


    override fun onOpenEditor() {
        Log.i(TAG, "onOpenEditor")
        viewModel.initScript(path = "", source = EditorMode.LOCAL)
        val action = HomeFragmentDirections.actionHomeFragmentToEditorFragment()
        navigate(action)
    }

    override fun onOpenScripts() {
        Log.i(TAG, "onOpenScripts")
        val action = HomeFragmentDirections.actionHomeFragmentToScriptsFragment()
        navigate(action)
    }

    override fun onOpenExplorer() {
        Log.i(TAG, "onOpenExplorer")
        val action = HomeFragmentDirections.actionHomeFragmentToExplorerFragment()
        navigate(action)
    }

    override fun onHelp() {
        try {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.home_help_link))
            )
            startActivity(browserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpenTerminal() {
        Log.i(TAG, "onOpenTerminal")
        viewModel.initScript(path = "", source = EditorMode.LOCAL)
        val action = HomeFragmentDirections.actionHomeFragmentToTerminalFragment()
        navigate(action)
    }

    override fun onFindDevices() {
        Log.i(TAG, "onFindDevices")
        boardManager?.detectUsbDevices()
    }

    override fun onApproveDevice(usbDevice: UsbDevice) {
        Log.i(TAG, "onApproveDevice")
        boardManager?.approveDevice(usbDevice)
    }

    override fun onForgetDevice(usbDevice: UsbDevice) {
        Log.i(TAG, "onForgetDevice")
        boardManager?.onForgetDevice(usbDevice)
    }

    override fun onDisconnectDevice() {
        Log.i(TAG, "onDisconnectDevice")
        boardManager?.onDisconnectDevice()
    }

    override fun onDenyDevice() {
        Log.i(TAG, "onDenyDevice")
        boardManager?.onDenyDevice()
    }

    override fun onReset() {
        viewModel.microDevice?.let {
            terminalManager?.resetDevice(it) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.terminal_reset_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSoftReset() {
        viewModel.microDevice?.let {
            terminalManager?.softResetDevice {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.terminal_soft_reset_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onTerminate() {
        terminalManager?.terminateExecution {
            Toast.makeText(
                requireContext(),
                getString(R.string.terminal_terminate_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    companion object {
        private const val TAG = "HomeFragment"
    }


}