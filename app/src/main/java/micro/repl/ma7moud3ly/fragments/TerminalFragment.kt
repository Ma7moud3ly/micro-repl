/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.CommandsManager
import micro.repl.ma7moud3ly.ui.TerminalScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.utils.TerminalUiEvents
import micro.repl.ma7moud3ly.utils.ThemeMode


class TerminalFragment : BaseFragment(), TerminalUiEvents {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isDarkMode) {
                    TerminalScreen(
                        viewModel = viewModel,
                        uiEvents = this@TerminalFragment
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onConnectionChanges = { connected ->
            if (connected.not()) findNavController().popBackStack()
        }

        viewModel.terminalOutput.value = ""
        viewModel.terminalInput.value = ""

        if (viewModel.scriptContent.isNotEmpty()) {
            terminalManager?.executeScript(viewModel.scriptContent)
        } else {
            viewModel.terminalOutput.value = ""
            //boardManager?.writeCommand(CommandsManager.RESET)
            boardManager?.writeCommand(CommandsManager.REPL_MODE)
        }

    }

    override fun onRun(code: String) {
        viewModel.history.push(code)
        terminalManager?.eval(code)
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

    override fun onSoftReset() {
        terminalManager?.softResetDevice {
            Toast.makeText(
                requireContext(),
                getString(R.string.terminal_soft_reset_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onUp() {
        viewModel.history.up()?.let {
            viewModel.terminalInput.value = it
        }
    }

    override fun onDown() {
        viewModel.history.down()?.let {
            viewModel.terminalInput.value = it
        }
    }

    override fun onDarkMode() {
        ThemeMode.toggleMode(requireActivity())
    }

    override fun onDestroy() {
        onTerminate()
        super.onDestroy()
    }

}