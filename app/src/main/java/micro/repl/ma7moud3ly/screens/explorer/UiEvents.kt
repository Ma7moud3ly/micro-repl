package micro.repl.ma7moud3ly.screens.explorer

import micro.repl.ma7moud3ly.model.MicroFile

sealed interface ExplorerEvents {
    data class Run(val file: MicroFile) : ExplorerEvents
    data class OpenFolder(val file: MicroFile) : ExplorerEvents
    data class Remove(val file: MicroFile) : ExplorerEvents
    data class Rename(val file: MicroFile) : ExplorerEvents
    data class Edit(val file: MicroFile) : ExplorerEvents
    data class New(val file: MicroFile) : ExplorerEvents
    data class Export(val file: MicroFile) : ExplorerEvents
    data object Import : ExplorerEvents
    data object Refresh : ExplorerEvents
    data object Up : ExplorerEvents
}