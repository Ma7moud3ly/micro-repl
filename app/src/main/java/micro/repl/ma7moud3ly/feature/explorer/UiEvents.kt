package micro.repl.ma7moud3ly.feature.explorer

import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.model.MicroPath

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

sealed interface ExplorerCommand {
    data object OpenTerminal : ExplorerCommand
    data object OpenEditor : ExplorerCommand
    data object Back : ExplorerCommand
    data object Refreshing : ExplorerCommand
    data class Imported(val path: MicroPath) : ExplorerCommand
}
