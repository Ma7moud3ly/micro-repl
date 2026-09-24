/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers.port

import kotlinx.coroutines.CoroutineDispatcher

/**
 * The dispatchers shared code runs on.
 *
 * `Dispatchers.IO` exists only where there are threads to spare - the JVM and
 * native - so the platform says what blocking work should run on, and a target
 * without a thread pool can point [io] at whatever it does have.
 */
interface AppDispatchers {

    /** For work that blocks: file and port access. */
    val io: CoroutineDispatcher
}
