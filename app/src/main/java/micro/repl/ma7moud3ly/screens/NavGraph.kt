package micro.repl.ma7moud3ly.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.EditorState
import micro.repl.ma7moud3ly.model.asMicroScript
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
                openExplorer = {
                    navController.navigate(AppRoutes.Explorer)
                },
                openTerminal = {
                    navController.navigate(AppRoutes.Terminal())
                },
                openEditor = {
                    navController.navigate(AppRoutes.Editor())
                },
                openScripts = {
                    navController.navigate(AppRoutes.Scripts)
                }
            )
        }

        composable<AppRoutes.Terminal> { backStackEntry ->
            val terminal: AppRoutes.Terminal = backStackEntry.toRoute()
            val microScript = remember { terminal.script.asMicroScript() }
            TerminalScreen(
                microScript = microScript,
                viewModel = viewModel,
                terminalManager = terminalManager,
                boardManager = boardManager,
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Editor> { backStackEntry ->
            val editor: AppRoutes.Editor = backStackEntry.toRoute()
            val editorState = remember { EditorState(editor.script.asMicroScript(), editor.blank) }
            EditorScreen(
                canRun = { canRun },
                editorState = editorState,
                filesManager = filesManager,
                onRemoteRun = { s ->
                    navController.navigate(AppRoutes.Terminal(s.asJson))
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
                    navController.navigate(AppRoutes.Terminal(microScript.asJson))
                },
                openEditor = { microScript ->
                    navController.navigate(AppRoutes.Editor(microScript.asJson))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Scripts> {
            ScriptsScreen(
                onOpenLocalScript = { microScript ->
                    navController.navigate(AppRoutes.Editor(microScript.asJson))
                },
                onNewScript = {
                    navController.navigate(AppRoutes.Editor(blank = true))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}