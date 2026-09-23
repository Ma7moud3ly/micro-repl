package micro.repl.ma7moud3ly

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoutes : NavKey {

    @Serializable
    data object Home : AppRoutes

    @Serializable
    data object Explorer : AppRoutes

    @Serializable
    data object Scripts : AppRoutes

    @Serializable
    data object ThemePicker : AppRoutes

    @Serializable
    data object Terminal : AppRoutes

    @Serializable
    data object Editor : AppRoutes
}
