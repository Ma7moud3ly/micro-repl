/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers.port

/**
 * Where log lines go on this platform: logcat on Android, stdout on desktop, the
 * browser console on web.
 *
 * Keep message arguments cheap - plain interpolation of values that are already
 * strings. Release strips the call, but only strips the work behind an argument
 * when it can prove that work has no side effect.
 */
interface Logger {

    fun v(tag: String, message: String)

    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String)

    fun e(tag: String, message: String, error: Throwable? = null)
}
