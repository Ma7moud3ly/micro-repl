package micro.repl.ma7moud3ly.fragments

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
        viewModel.editorMode = EditorMode.LOCAL
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

    override fun onOpenTerminal() {
        Log.i(TAG, "onOpenTerminal")
        viewModel.script = ""
        viewModel.scriptPath.value = ""
        val action = HomeFragmentDirections.actionHomeFragmentToTerminalFragment()
        navigate(action)
    }

    override fun onFindDevices() {
        Log.i(TAG, "onFindDevices")
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