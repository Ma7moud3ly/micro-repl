package micro.repl.ma7moud3ly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.AppRoutes
import micro.repl.ma7moud3ly.screens.dialogs.ThemeSelectorDialog
import micro.repl.ma7moud3ly.screens.editor.EditorScreen
import micro.repl.ma7moud3ly.screens.explorer.FilesExplorerScreen
import micro.repl.ma7moud3ly.screens.home.HomeScreen
import micro.repl.ma7moud3ly.screens.scripts.ScriptsScreen
import micro.repl.ma7moud3ly.screens.terminal.TerminalScreen

@Composable
fun RootGraph(
    viewModel: MainViewModel,
    boardManager: BoardManager,
    filesManager: FilesManager,
    terminalManager: TerminalManager,
    navController: NavHostController = rememberNavController(),
) {
    var canRun by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.status.collect { status ->
            when (status) {
                is ConnectionStatus.Connected -> canRun = true
                else -> {
                    navController.popBackStack(AppRoutes.Home, inclusive = false)
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
                viewModel = viewModel,
                boardManager = boardManager,
                terminalManager = terminalManager,
                openThemePicker = {
                    navController.navigate(AppRoutes.ThemePicker)
                },
                openExplorer = {
                    navController.navigate(AppRoutes.Explorer)
                },
                openTerminal = {
                    // bare REPL - no script attached
                    viewModel.openScript(MicroScript())
                    navController.navigate(AppRoutes.Terminal)
                },
                openEditor = {
                    viewModel.openScript(MicroScript())
                    navController.navigate(AppRoutes.Editor())
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
            val microScript = remember { viewModel.script }
            TerminalScreen(
                microScript = microScript,
                viewModel = viewModel,
                terminalManager = terminalManager,
                boardManager = boardManager,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<AppRoutes.Editor> { backStackEntry ->
            val editor: AppRoutes.Editor = backStackEntry.toRoute()
            val script = remember { viewModel.script }
            EditorScreen(
                canRun = { canRun },
                script = script,
                blank = editor.blank,
                filesManager = filesManager,
                openThemePicker = {
                    navController.navigate(AppRoutes.ThemePicker)
                },
                onRemoteRun = { s ->
                    viewModel.openScript(s)
                    navController.navigate(AppRoutes.Terminal)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Explorer> {
            FilesExplorerScreen(
                filesManager = filesManager,
                viewModel = viewModel,
                terminalManager = terminalManager,
                openTerminal = { microScript ->
                    viewModel.openScript(microScript)
                    navController.navigate(AppRoutes.Terminal)
                },
                openEditor = { microScript ->
                    viewModel.openScript(microScript)
                    navController.navigate(AppRoutes.Editor())
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Scripts> {
            ScriptsScreen(
                onOpenLocalScript = { microScript ->
                    viewModel.openScript(microScript)
                    navController.navigate(AppRoutes.Editor())
                },
                onRunLocalScript = { microScript ->
                    viewModel.openScript(microScript)
                    navController.navigate(AppRoutes.Terminal)
                },
                onNewScript = {
                    viewModel.openScript(MicroScript())
                    navController.navigate(AppRoutes.Editor(blank = true))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}