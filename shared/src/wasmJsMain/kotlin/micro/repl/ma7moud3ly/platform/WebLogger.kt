/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import micro.repl.ma7moud3ly.BuildInfo

actual val AppLog: Logger = WebLogger()

@OptIn(ExperimentalWasmJsInterop::class)
private fun consoleDebug(message: String): Unit = js("console.debug(message)")
@OptIn(ExperimentalWasmJsInterop::class)
private fun consoleInfo(message: String): Unit = js("console.info(message)")
@OptIn(ExperimentalWasmJsInterop::class)
private fun consoleWarn(message: String): Unit = js("console.warn(message)")
@OptIn(ExperimentalWasmJsInterop::class)
private fun consoleError(message: String): Unit = js("console.error(message)")

/**
 * Writes to the browser console while [enabled], one console method per level,
 * and does nothing otherwise.
 */
class WebLogger(
    private val enabled: Boolean = BuildInfo.DEBUG
) : Logger {

    override fun v(tag: String, message: String) {
        if (enabled) consoleDebug("$tag: $message")
    }

    override fun d(tag: String, message: String) {
        if (enabled) consoleDebug("$tag: $message")
    }

    override fun i(tag: String, message: String) {
        if (enabled) consoleInfo("$tag: $message")
    }

    override fun w(tag: String, message: String) {
        if (enabled) consoleWarn("$tag: $message")
    }

    override fun e(tag: String, message: String, error: Throwable?) {
        if (!enabled) return
        consoleError("$tag: $message")
        if (error != null) consoleError(error.stackTraceToString())
    }
}
