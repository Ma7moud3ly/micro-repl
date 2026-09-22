/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.Single

/**
 * The script being handed from one screen to the next.
 *
 */
@Single
class ScriptStoreManager {

    /** The script the next screen should work on. */
    var script: MicroScript = MicroScript()
        private set

    /** Whether the editor should start empty rather than restoring the last script. */
    var blank: Boolean = false
        private set

    /** Hands [script] to the next screen. Pass `MicroScript()` for a bare session. */
    fun open(script: MicroScript, blank: Boolean = false) {
        this.script = script
        this.blank = blank
    }
}
