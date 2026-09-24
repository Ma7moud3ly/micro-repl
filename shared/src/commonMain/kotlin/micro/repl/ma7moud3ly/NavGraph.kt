package micro.repl.ma7moud3ly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import micro.repl.ma7moud3ly.feature.editor.EditorScreen
import micro.repl.ma7moud3ly.feature.explorer.FilesExplorerScreen
import micro.repl.ma7moud3ly.feature.home.HomeScreen
import micro.repl.ma7moud3ly.feature.scripts.ScriptsScreen
import micro.repl.ma7moud3ly.feature.terminal.TerminalScreen
import micro.repl.ma7moud3ly.ui.dialog.ThemeSelectorDialog
import micro.repl.ma7moud3ly.model.ConnectionStatus

@Composable
fun RootGraph(viewModel: MainViewModel) {
    val backStack = rememberNavBackStack(NavSavedState, AppRoutes.Home)
    var canRun by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.status.collect { status ->
            when (status) {
                is ConnectionStatus.Connected -> canRun = true
                else -> {
                    canRun = false
                    // If the board is disconnected, go back to Home.
                    // Don't go back to Home when the Editor is open,
                    // so the current script remains open.
                    if (backStack.lastOrNull() != AppRoutes.Editor) backStack.popToHome()
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.pop() },
        sceneStrategies = listOf(DialogSceneStrategy()),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            entry<AppRoutes.Home> {
                HomeScreen(
                    openThemePicker = { backStack.add(AppRoutes.ThemePicker) },
                    openExplorer = { backStack.add(AppRoutes.Explorer) },
                    openTerminal = { backStack.add(AppRoutes.Terminal) },
                    openEditor = { backStack.add(AppRoutes.Editor) },
                    openScripts = { backStack.add(AppRoutes.Scripts) }
                )
            }

            entry<AppRoutes.ThemePicker>(metadata = DialogSceneStrategy.dialog()) {
                ThemeSelectorDialog(onDismiss = { backStack.pop() })
            }

            entry<AppRoutes.Terminal> {
                TerminalScreen(onBack = { backStack.pop() })
            }

            entry<AppRoutes.Editor> {
                EditorScreen(
                    openThemePicker = { backStack.add(AppRoutes.ThemePicker) },
                    onRemoteRun = { backStack.add(AppRoutes.Terminal) },
                    onBack = {
                        // Only the explorer is dead without a board, so that's the
                        // one worth skipping past. Scripts works offline - go back to it as normal.
                        val previous = backStack.getOrNull(backStack.lastIndex - 1)
                        val skip = canRun.not() && previous == AppRoutes.Explorer
                        if (skip) backStack.popToHome() else backStack.pop()
                    }
                )
            }

            entry<AppRoutes.Explorer> {
                FilesExplorerScreen(
                    openTerminal = { backStack.add(AppRoutes.Terminal) },
                    openEditor = { backStack.add(AppRoutes.Editor) },
                    onBack = { backStack.pop() }
                )
            }

            entry<AppRoutes.Scripts> {
                ScriptsScreen(
                    canRun = { canRun },
                    onOpenLocalScript = { backStack.add(AppRoutes.Editor) },
                    onRunLocalScript = { backStack.add(AppRoutes.Terminal) },
                    onNewScript = { backStack.add(AppRoutes.Editor) },
                    onBack = { backStack.pop() }
                )
            }
        }
    )
}

/** Leaves the current screen. Home is never popped - it is the root. */
private fun NavBackStack<NavKey>.pop() {
    if (size > 1) removeAt(lastIndex)
}

/** Unwinds everything above Home. */
private fun NavBackStack<NavKey>.popToHome() {
    while (size > 1) removeAt(lastIndex)
}
