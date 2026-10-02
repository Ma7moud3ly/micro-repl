package micro.repl.ma7moud3ly

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

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

/**
 * How the back stack is saved and restored.
 *
 * Every route has to be registered here, or restoring a back stack that holds it
 * fails.
 */
val NavSavedState = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoutes.Home::class)
            subclass(AppRoutes.Explorer::class)
            subclass(AppRoutes.Scripts::class)
            subclass(AppRoutes.ThemePicker::class)
            subclass(AppRoutes.Terminal::class)
            subclass(AppRoutes.Editor::class)
        }
    }
}
