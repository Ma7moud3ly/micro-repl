package micro.repl.ma7moud3ly.feature.terminal

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.terminal_soft_reset_msg
import micro.repl.ma7moud3ly.shared.resources.terminal_terminate_msg
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.components.asSuccessMessage
import micro.repl.ma7moud3ly.ui.components.rememberMessageState
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val messageToast = rememberMessageState()

    LaunchedEffect(Unit) {
        viewModel.commands.collect { command ->
            val text = getString(
                when (command) {
                    TerminalCommand.Terminated -> Res.string.terminal_terminate_msg
                    TerminalCommand.SoftReset -> Res.string.terminal_soft_reset_msg
                }
            )
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

