package micro.repl.ma7moud3ly.screens.terminal

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.TerminalCommand
import micro.repl.ma7moud3ly.model.asSuccessMessage
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.components.rememberMessageState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val messageToast = rememberMessageState()
    val terminateMessage = stringResource(R.string.terminal_terminate_msg)
    val softResetMessage = stringResource(R.string.terminal_soft_reset_msg)

    LaunchedEffect(Unit) {
        viewModel.commands.collect { command ->
            val text = when (command) {
                TerminalCommand.Terminated -> terminateMessage
                TerminalCommand.SoftReset -> softResetMessage
            }
            messageToast.show(text.asSuccessMessage)
        }
    }


    BackHandler(enabled = true, onBack)

    MessageToast(state = messageToast)

    TerminalScreenContent(
        microScript = { viewModel.script },
        terminalInput = { viewModel.input },
        terminalOutput = { viewModel.output },
        onInputChanges = viewModel::onInputChange,
        uiEvents = {
            when (it) {
                TerminalEvents.Run -> viewModel.run()
                TerminalEvents.Terminate -> viewModel.terminate(notify = true)
                TerminalEvents.SoftReset -> viewModel.softReset()
                TerminalEvents.Clear -> viewModel.clear()
                TerminalEvents.MoveUp -> viewModel.historyUp()
                TerminalEvents.MoveDown -> viewModel.historyDown()
                TerminalEvents.Back -> onBack()
            }
        }
    )
}

