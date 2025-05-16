package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState

private val microFile = MicroFile(
    name = "",
    path = "lib",
    type = MicroFile.FILE,
    size = 300000
)


private val microDir = MicroFile(
    name = "",
    path = "lib",
    type = MicroFile.DIRECTORY
)

@Preview
@Composable
private fun FileCreateDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileCreateDialog(
            microFile = { microFile },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileCreateDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        FileCreateDialog(
            microFile = { microFile },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileCreateDialogPreviewLight2() {
    AppTheme(darkTheme = false) {
        FileCreateDialog(
            microFile = { microDir },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileCreateDialogPreviewDark2() {
    AppTheme(darkTheme = true) {
        FileCreateDialog(
            microFile = { microDir },
            onOk = {}
        )
    }
}

@Composable
fun FileCreateDialog(
    state: MyDialogState = rememberMyDialogState(visible = true),
    microFile: () -> MicroFile?,
    onOk: (file: MicroFile) -> Unit
) {
    val file = microFile() ?: return
    val message = if (file.name.isEmpty()) {
        stringResource(id = R.string.explorer_create) + " " + stringResource(
            id = if (file.isFile) R.string.explorer_file_new
            else R.string.explorer_new_folder
        ) + "~/" + file.path
    } else stringResource(
        id = R.string.explorer_rename_label, file.name
    )

    val name by remember(file) {
        derivedStateOf {
            if (file.name.isNotEmpty()) file.name
            else if (file.isFile) "main.py"
            else "folder"
        }
    }

    MyDialog(
        state = state,
        dismissOnClickOutside = false
    ) {
        InputDialogContent(
            name = name,
            message = message,
            onDismiss = { state.dismiss() },
            onOk = { fileName ->
                val newFile = MicroFile(
                    name = fileName,
                    path = file.path,
                    type = if (file.isFile) MicroFile.FILE
                    else MicroFile.DIRECTORY
                )
                state.dismiss()
                onOk(newFile)
            }
        )
    }
}

