package micro.repl.ma7moud3ly.feature.scripts

import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.RecentScript

sealed interface ScriptsEvents {
    data class Run(val script: MicroScript) : ScriptsEvents
    data class Open(val script: MicroScript) : ScriptsEvents
    data class Delete(val script: MicroScript) : ScriptsEvents
    data class Rename(val script: MicroScript) : ScriptsEvents
    data class Share(val script: MicroScript) : ScriptsEvents
    data class OpenRecent(val recent: RecentScript) : ScriptsEvents
    data class RunRecent(val recent: RecentScript) : ScriptsEvents
    data class RemoveRecent(val recent: RecentScript) : ScriptsEvents
    data object NewScript : ScriptsEvents
    data object Back : ScriptsEvents
}