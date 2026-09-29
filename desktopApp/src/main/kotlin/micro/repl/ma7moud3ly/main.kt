package micro.repl.ma7moud3ly

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import micro.repl.ma7moud3ly.di.AppModule
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.app_name
import micro.repl.ma7moud3ly.shared.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.modules

fun main() {
    startKoin {
        printLogger(Level.INFO)
        modules(AppModule::class)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.logo),
        ) {
            MicroReplApp()
        }
    }
}
