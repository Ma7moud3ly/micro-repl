package micro.repl.ma7moud3ly.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.CommandsManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
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

    LaunchedEffect(Unit) {
        viewModel.status.collect { status ->
            if (status.isConnected.not()) {
                navController.popBackStack(AppRoutes.Home, inclusive = false)
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
                    viewModel.initScript(path = "", source = EditorMode.LOCAL)
                    navController.navigate(AppRoutes.Terminal)
                },
                openEditor = {
                    viewModel.initScript(path = "", source = EditorMode.LOCAL)
                    navController.navigate(AppRoutes.Editor)
                },
                openScripts = {
                    navController.navigate(AppRoutes.Scripts)
                }
            )
        }

        composable<AppRoutes.Terminal> {
            TerminalScreen(
                viewModel = viewModel,
                terminalManager = terminalManager,
                enterReplModel = {
                    boardManager.writeCommand(CommandsManager.REPL_MODE)
                }
            )
        }

        composable<AppRoutes.Editor> {
            EditorScreen(
                filesManager = filesManager,
                script = MicroScript(),
                onRemoteRun = { script ->
                    viewModel.initScript(path = script.path, content = script.content)
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
                openTerminal = {
                    navController.navigate(AppRoutes.Terminal)
                },
                openEditor = {
                    //viewModel.initScript(path = "", source = EditorMode.LOCAL)
                    navController.navigate(AppRoutes.Editor)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Scripts> {
            ScriptsScreen(
                onBack = { navController.popBackStack() },
                onOpen = { script ->
                    viewModel.initScript(path = script.path, source = EditorMode.LOCAL)
                    navController.navigate(AppRoutes.Editor)
                }
            )
        }
    }
}