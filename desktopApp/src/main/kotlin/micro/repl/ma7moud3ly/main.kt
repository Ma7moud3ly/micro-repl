package micro.repl.ma7moud3ly

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import micro.repl.ma7moud3ly.di.AppModule
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.app_name
import micro.repl.ma7moud3ly.shared.resources.logo
import micro.repl.ma7moud3ly.ui.window.DesktopTitleBar
import micro.repl.ma7moud3ly.ui.window.ResizableWindowFrame
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.modules
import java.awt.Dimension

fun main() {
    startKoin {
        printLogger(Level.INFO)
        modules(AppModule::class)
    }
    application {
        val state = rememberWindowState(placement = WindowPlacement.Maximized)
        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.logo),
            undecorated = true
        ) {
            LaunchedEffect(Unit) { window.minimumSize = Dimension(480, 360) }
            ResizableWindowFrame(state = state) {
                MicroReplApp(
                    topBar = { DesktopTitleBar(state = state, onClose = ::exitApplication) }
                )
            }
        }
    }
}
