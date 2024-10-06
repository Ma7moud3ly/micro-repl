package micro.repl.ma7moud3ly.screens.scripts

import micro.repl.ma7moud3ly.model.MicroScript

sealed interface ScriptsEvents {
    data class Run(val script: MicroScript) : ScriptsEvents
    data class Open(val script: MicroScript) : ScriptsEvents
    data class Delete(val script: MicroScript) : ScriptsEvents
    data class Rename(val script: MicroScript) : ScriptsEvents
    data object Back : ScriptsEvents
}