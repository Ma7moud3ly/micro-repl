/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The platforms the app is built for. [label] is the name to show a user.
 */
enum class Platform(val label: String) {
    Android("Android"),
    Desktop("Desktop"),
    Web("Web")
}

/** The platform this code is running on. */
expect val currentPlatform: Platform

val Platform.isMobile: Boolean get() = this == Platform.Android

val Platform.isWeb: Boolean get() = this == Platform.Web

val Platform.isDesktop: Boolean get() = this == Platform.Desktop

/**
 * The platform the surrounding composition renders for.
 *
 * Defaults to [currentPlatform]; provide it to render another platform's layout,
 * as in a `@Preview`.
 */
val LocalPlatform = staticCompositionLocalOf { currentPlatform }
