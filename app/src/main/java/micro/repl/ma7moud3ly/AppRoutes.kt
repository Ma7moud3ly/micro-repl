package micro.repl.ma7moud3ly

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    data object Home

    @Serializable
    data object Explorer

    @Serializable
    data object Scripts

    @Serializable
    data object ThemePicker

    @Serializable
    data object Terminal

    @Serializable
    data object Editor
}
