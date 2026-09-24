package micro.repl.ma7moud3ly

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import micro.repl.ma7moud3ly.di.AppModule
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
            title = "Micro REPL",
        ) {
            MicroReplApp()
        }
    }
}
