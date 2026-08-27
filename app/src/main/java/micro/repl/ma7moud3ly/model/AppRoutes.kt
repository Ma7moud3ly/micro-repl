package micro.repl.ma7moud3ly.model

import kotlinx.serialization.Serializable

/**
 * Navigation routes.
 *
 * Routes carry no script data - the script is handed over through
 * `MainViewModel.openScript()`. Passing it here would put the whole file
 * content into the route string and the saved-state Bundle.
 */
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
    data class Editor(val blank: Boolean = false)
}
