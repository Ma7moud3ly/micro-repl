/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import micro.repl.ma7moud3ly.BuildConfig
import micro.repl.ma7moud3ly.managers.port.Logger
import micro.repl.ma7moud3ly.platform.AndroidLogger

/**
 * The logger the whole app writes to: `AppLog.i(TAG, "...")`.
 */
val AppLog: Logger = platformLogger()

/**
 * The platform's logger.
 *
 * Becomes `expect fun platformLogger(): Logger` once the shared module lands, with
 * each target supplying its own `actual` - this file is the only place that names
 * an implementation.
 */
internal fun platformLogger(): Logger = AndroidLogger()

/**
 * Whether this is a debug build.
 *
 * Guard a log whose message is expensive to build - release strips the call but
 * not the work that produced its arguments.
 *
 * Becomes `expect val isDebug: Boolean` alongside [platformLogger].
 */
val isDebug: Boolean = BuildConfig.DEBUG
