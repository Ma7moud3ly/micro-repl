/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.scripts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.port.ScriptsManager
import micro.repl.ma7moud3ly.managers.AppLog
import micro.repl.ma7moud3ly.managers.ScriptStoreManager
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.KoinViewModel

private const val TAG = "ScriptsViewModel"


@KoinViewModel
class ScriptsViewModel(
    private val scriptsManager: ScriptsManager,
    private val scriptStoreManager: ScriptStoreManager
) : ViewModel() {

    /** Scripts on this device. Backed by a snapshot list, so edits recompose. */
    val scripts: List<MicroScript> get() = scriptsManager.scripts

    /** The script a rename or delete dialog is about to act on. */
    var selectedScript by mutableStateOf<MicroScript?>(null)
        private set

    fun selectScript(script: MicroScript) {
        selectedScript = script
    }

    fun renameSelectedScript(newName: String) {
        val script = selectedScript ?: return
        viewModelScope.launch { scriptsManager.renameScript(script, newName) }
    }

    fun deleteSelectedScript() {
        val script = selectedScript ?: return
        viewModelScope.launch { scriptsManager.deleteScript(script) }
    }

    fun shareScript(script: MicroScript) {
        scriptsManager.shareScript(script)
    }

    /** Hands a blank script to the editor, which should start empty. */
    fun newScript() {
        scriptStoreManager.open(MicroScript(), blank = true)
    }

    /**
     * Reads [script] off disk and hands it to the screen being opened.
     *
     * @return false if the file could not be read, in which case nothing is handed over.
     */
    suspend fun handOff(script: MicroScript): Boolean = try {
        script.content = scriptsManager.read(script.file)
        AppLog.v(TAG, script.toString())
        scriptStoreManager.open(script)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    init {
        viewModelScope.launch { scriptsManager.refresh() }
    }
}
