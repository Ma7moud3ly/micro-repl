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

private const val TAG = "ScriptsScreen"

@Composable
fun ScriptsScreen(
    onBack: () -> Unit,
    onOpenLocalScript: (MicroScript) -> Unit
) {
    val context = LocalContext.current
    val scriptsManager = remember { ScriptsManager(context) }
    var showFileRenameDialog by remember { mutableStateOf(false) }
    var showFileDeleteDialog by remember { mutableStateOf(false) }
    var selectedScript by remember { mutableStateOf<MicroScript?>(null) }
    val scripts = remember { scriptsManager.scripts }

    FileRenameDialog(
        name = { selectedScript?.name.orEmpty() },
        show = { showFileRenameDialog && selectedScript != null },
        onOk = { newName ->
            showFileRenameDialog = false
            scriptsManager.renameScript(selectedScript!!, newName)
        },
        onDismiss = { showFileRenameDialog = false }
    )

    FileDeleteDialog(
        name = { selectedScript?.name.orEmpty() },
        show = { showFileDeleteDialog && selectedScript != null },
        onOk = {
            showFileDeleteDialog = false
            scriptsManager.deleteScript(selectedScript!!)
        },
        onDismiss = { showFileDeleteDialog = false }
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
                is ScriptsEvents.Open -> readLocalScript(it.script)
                is ScriptsEvents.Delete -> {
                    selectedScript = it.script
                    showFileDeleteDialog = true
                }

                is ScriptsEvents.Rename -> {
                    selectedScript = it.script
                    showFileRenameDialog = true
                }

                is ScriptsEvents.Run -> {}
            }
        }
    )
}