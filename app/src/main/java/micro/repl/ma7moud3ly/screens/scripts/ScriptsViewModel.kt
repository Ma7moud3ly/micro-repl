/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.scripts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.port.ScriptsManager
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.KoinViewModel

private const val TAG = "ScriptsViewModel"


@KoinViewModel
class ScriptsViewModel(
    private val scriptsManager: ScriptsManager
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

    /**
     * Loads [script]'s content from disk.
     *
     * @return the script with its content filled in, or null if it could not be read.
     */
    suspend fun loadScript(script: MicroScript): MicroScript? = try {
        script.content = scriptsManager.read(script.file)
        Log.v(TAG, script.toString())
        script
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    init {
        // The list used to be scanned in the manager's constructor, on whichever
        // thread first injected it.
        viewModelScope.launch { scriptsManager.refresh() }
    }
}
