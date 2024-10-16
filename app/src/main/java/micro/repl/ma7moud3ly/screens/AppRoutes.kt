package micro.repl.ma7moud3ly.screens

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    data object Home

    @Serializable
    data object Explorer

    @Serializable
    data object Scripts

    @Serializable
    data class Terminal(val script: String = "")

    @Serializable
    data class Editor(val script: String = "", val blank: Boolean = false)
}