/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.explorer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

/** What the picker offers: scripts and the archives they arrive in. */
private val acceptedFiles = setOf("py", "txt", "json")

/**
 * Opens the platform's file picker and hands back what was chosen.
 *
 * @param onPicked the file's name and its bytes, called only if one was picked.
 * @return the function to call to open the picker.
 */
@Composable
fun rememberFilesPicker(onPicked: (String, ByteArray) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(acceptedFiles)
    ) { file ->
        // reading suspends, so it runs on the screen's scope rather than here
        if (file != null) scope.launch { onPicked(file.name, file.readBytes()) }
    }
    return { launcher.launch() }
}
