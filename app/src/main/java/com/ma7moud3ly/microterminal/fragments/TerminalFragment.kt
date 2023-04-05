package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.managers.TerminalUiEvents
import com.ma7moud3ly.microterminal.ui.TerminalScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.utils.ThemeMode


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

        if (viewModel.script.isNotEmpty()) {
            viewModel.newExecution = true
            terminalManager?.eval(viewModel.script)
        }
    }

    override fun onExecute(code: String) {
        viewModel.history.push(code)
        viewModel.newExecution = true
        terminalManager?.execute(code)
    }

    override fun onTerminate() {
        viewModel.newExecution = true
        terminalManager?.terminateExecution {
            Toast.makeText(
                requireContext(),
                getString(R.string.terminal_terminate_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSoftReset() {
        viewModel.newExecution = true
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
}