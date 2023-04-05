package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.ui.HomeScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.utils.EditorMode
import com.ma7moud3ly.microterminal.utils.HomeUiEvents

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
        viewModel.editorMode = EditorMode.LOCAL
        val action = HomeFragmentDirections.actionHomeFragmentToEditorFragment()
        navigate(action)
    }

    override fun onOpenScripts() {
        val action = HomeFragmentDirections.actionHomeFragmentToScriptsFragment()
        navigate(action)
    }

    override fun onOpenExplorer() {
        val action = HomeFragmentDirections.actionHomeFragmentToExplorerFragment()
        navigate(action)
    }

    override fun onOpenTerminal() {
        viewModel.script = ""
        viewModel.scriptPath.value = ""
        val action = HomeFragmentDirections.actionHomeFragmentToTerminalFragment()
        navigate(action)
    }

    override fun onFindDevices() {
        deviceManager?.detectUsbDevices()
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
            terminalManager?.softResetDevice(it) {
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