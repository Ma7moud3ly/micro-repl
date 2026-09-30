package micro.repl.ma7moud3ly

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import micro.repl.ma7moud3ly.di.AppModule
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.modules

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        printLogger(Level.INFO)
        modules(AppModule::class)
    }
    ComposeViewport {
        MicroReplApp()
    }
}
