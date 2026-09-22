package micro.repl.ma7moud3ly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import micro.repl.ma7moud3ly.model.AppRoutes
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.feature.dialogs.ThemeSelectorDialog
import micro.repl.ma7moud3ly.feature.editor.EditorScreen
import micro.repl.ma7moud3ly.feature.explorer.FilesExplorerScreen
import micro.repl.ma7moud3ly.feature.home.HomeScreen
import micro.repl.ma7moud3ly.feature.scripts.ScriptsScreen
import micro.repl.ma7moud3ly.feature.terminal.TerminalScreen

@Composable
fun RootGraph(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController(),
) {
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
                    val destination = navController.currentDestination
                    if (destination?.hasRoute(AppRoutes.Editor::class) == false) {
                        navController.popBackStack(AppRoutes.Home, inclusive = false)
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home
    ) {
        composable<AppRoutes.Home> {
            HomeScreen(
                openThemePicker = {
                    navController.navigate(AppRoutes.ThemePicker)
                },
                openExplorer = {
                    navController.navigate(AppRoutes.Explorer)
                },
                openTerminal = {
                    navController.navigate(AppRoutes.Terminal)
                },
                openEditor = {
                    navController.navigate(AppRoutes.Editor)
                },
                openScripts = {
                    navController.navigate(AppRoutes.Scripts)
                }
            )
        }

        dialog<AppRoutes.ThemePicker> {
            ThemeSelectorDialog(onDismiss = { navController.popBackStack() })
        }

        composable<AppRoutes.Terminal> {
            TerminalScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<AppRoutes.Editor> {
            EditorScreen(
                openThemePicker = {
                    navController.navigate(AppRoutes.ThemePicker)
                },
                onRemoteRun = {
                    navController.navigate(AppRoutes.Terminal)
                },
                onBack = {
                    // Only the explorer is dead without a board, so that's the
                    // one worth skipping past. Scripts works offline - go back to it as normal.
                    val previous = navController.previousBackStackEntry?.destination
                    val skip = canRun.not() && previous?.hasRoute(AppRoutes.Explorer::class) == true
                    if (skip) navController.popBackStack(AppRoutes.Home, inclusive = false)
                    else navController.popBackStack()
                }
            )
        }

        composable<AppRoutes.Explorer> {
            FilesExplorerScreen(
                openTerminal = {
                    navController.navigate(AppRoutes.Terminal)
                },
                openEditor = {
                    navController.navigate(AppRoutes.Editor)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Scripts> {
            ScriptsScreen(
                canRun = { canRun },
                onOpenLocalScript = {
                    navController.navigate(AppRoutes.Editor)
                },
                onRunLocalScript = {
                    navController.navigate(AppRoutes.Terminal)
                },
                onNewScript = {
                    navController.navigate(AppRoutes.Editor)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}