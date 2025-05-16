/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.scripts

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import micro.repl.ma7moud3ly.managers.ScriptsManager
import micro.repl.ma7moud3ly.screens.dialogs.FileDeleteDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileRenameDialog
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState

private const val TAG = "ScriptsScreen"

@Composable
fun ScriptsScreen(
    onBack: () -> Unit,
    onNewScript: () -> Unit,
    onOpenLocalScript: (MicroScript) -> Unit
) {
    val context = LocalContext.current
    val scriptsManager = remember { ScriptsManager(context) }
    val renameFileDialog = rememberMyDialogState()
    val deleteFileDialog = rememberMyDialogState()
    var selectedScript by remember { mutableStateOf<MicroScript?>(null) }
    val scripts = remember { scriptsManager.scripts }

    FileRenameDialog(
        state = renameFileDialog,
        name = { selectedScript?.name.orEmpty() },
        onOk = { newName ->
            scriptsManager.renameScript(selectedScript!!, newName)
        }
    )

    FileDeleteDialog(
        state = deleteFileDialog,
        name = { selectedScript?.name.orEmpty() },
        onOk = {
            scriptsManager.deleteScript(selectedScript!!)
        }
    )

    fun readLocalScript(script: MicroScript) {
        try {
            val content = scriptsManager.read(script.file)
            script.content = content
            Log.v(TAG, script.toString())
            onOpenLocalScript(script)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ScriptsScreenContent(
        scripts = { scripts },
        uiEvents = {
            when (it) {
                is ScriptsEvents.Back -> onBack()
                is ScriptsEvents.NewScript -> onNewScript()
                is ScriptsEvents.Open -> readLocalScript(it.script)
                is ScriptsEvents.Share -> scriptsManager.shareScript(it.script)
                is ScriptsEvents.Delete -> {
                    selectedScript = it.script
                    deleteFileDialog.show()
                }

                is ScriptsEvents.Rename -> {
                    selectedScript = it.script
                    renameFileDialog.show()
                }

                is ScriptsEvents.Run -> {}
            }
        }
    )
}