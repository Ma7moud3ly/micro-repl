/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.dialog_cancel
import micro.repl.ma7moud3ly.shared.resources.dialog_no
import micro.repl.ma7moud3ly.shared.resources.dialog_ok
import micro.repl.ma7moud3ly.shared.resources.dialog_yes
import micro.repl.ma7moud3ly.ui.components.MyButton
import org.jetbrains.compose.resources.stringResource

/**
 * The bodies every dialog in the app is built from.
 *
 * A dialog belongs to the feature that opens it; what they share is these two
 * shapes - ask for a name, or ask yes/no.
 */

/** A prompt with a single text field: rename, save-as, new file. */
@Composable
internal fun InputDialogContent(
    name: String,
    message: String,
    onDismiss: () -> Unit,
    onOk: (String) -> Unit
) {
    var fileName by remember { mutableStateOf(name) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center, maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        BasicTextField(
            value = fileName,
            onValueChange = { fileName = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White
                )
                .padding(vertical = 16.dp, horizontal = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MyButton(
                text = stringResource(Res.string.dialog_ok),
                modifier = Modifier.weight(0.4f),
                onClick = { onOk(fileName) }
            )
            MyButton(
                text = stringResource(Res.string.dialog_cancel),
                background = MaterialTheme.colorScheme.secondary,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.weight(0.4f),
                onClick = onDismiss
            )
        }
    }
}

/** A yes/no prompt: delete, discard, overwrite. */
@Composable
internal fun ApproveDialogContent(
    message: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    textAlign: TextAlign = TextAlign.Center,
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = style,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = textAlign
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MyButton(
                text = stringResource(Res.string.dialog_yes),
                modifier = Modifier.weight(0.4f),
                onClick = onOk
            )
            MyButton(
                text = stringResource(Res.string.dialog_no),
                background = MaterialTheme.colorScheme.secondary,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.weight(0.4f),
                onClick = onDismiss
            )
        }
    }
}
