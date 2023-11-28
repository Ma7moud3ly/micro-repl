/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.dividerColor
import micro.repl.ma7moud3ly.ui.theme.editorIconSize
import micro.repl.ma7moud3ly.utils.MicroScript
import micro.repl.ma7moud3ly.utils.ScriptsUiEvents


@SuppressLint("UnrememberedMutableState")
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ScriptsScreenPreview() {
    val scripts = mutableListOf(
        MicroScript("Main.py", ""),
        MicroScript("Main.M", "")
    )
    AppTheme(darkTheme = true) {
        ScriptsScreen(scripts)
    }
}

@Composable
fun ScriptsScreen(
    scripts: List<MicroScript>,
    uiEvents: ScriptsUiEvents? = null,
) {
    Scaffold {
        Box(Modifier.padding(it)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                Header(onUp = { uiEvents?.onUp() })

                if (scripts.isNotEmpty()) scripts.forEach { script ->
                    ItemScript(
                        script = script,
                        onOpen = { uiEvents?.onOpen(script) },
                        onRename = { uiEvents?.onRename(script) },
                        onDelete = { uiEvents?.onDelete(script) },
                        onRun = { uiEvents?.onRun(script) },
                    )
                } else Text(
                    text = "No scripts saved yet!",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

            }
        }
    }
}

@Composable
private fun Header(onUp: () -> Unit) {
    val title =
        stringResource(id = R.string.home_scripts) + " :: " +
                stringResource(id = R.string.this_device)
    Column(
        Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconHeader(
                title = R.string.explorer_up,
                icon = R.drawable.arrow_up,
                tint = MaterialTheme.colorScheme.primary,
                onClick = onUp
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = dividerColor
        )
    }
}

@Composable
private fun ItemScript(
    script: MicroScript,
    onOpen: (script: MicroScript) -> Unit,
    onDelete: (script: MicroScript) -> Unit,
    onRename: (script: MicroScript) -> Unit,
    onRun: (script: MicroScript) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp) //margin
            .background(
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onOpen.invoke(script) },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = script.name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = stringResource(id = R.string.explorer_edit),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(editorIconSize)
                .clickable { onRename.invoke(script) }
        )
        Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = stringResource(id = R.string.explorer_delete),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(editorIconSize)
                .clickable { onDelete.invoke(script) }
        )
        /*if (script.isPython) Icon(
            painter = painterResource(id = R.drawable.run),
            contentDescription = stringResource(id = R.string.explorer_run),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(editorIconSize)
                .clickable { onRun.invoke(script) }
        )*/
    }
}
