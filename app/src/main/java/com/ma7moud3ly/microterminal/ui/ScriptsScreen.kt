package com.ma7moud3ly.microterminal.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.ui.theme.grey100
import com.ma7moud3ly.microterminal.util.Script


@SuppressLint("UnrememberedMutableState")
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ScriptsScreenPreview() {
    val scripts = mutableListOf(
        Script("Main.py", ""),
        Script("Main.M", "")
    )
    ScriptsScreen(scripts, {}, {}, {},{})
}

@Composable
fun ScriptsScreen(
    scripts: List<Script>,
    onOpen: (script: Script) -> Unit,
    onDelete: (script: Script) -> Unit,
    onRename: (script: Script) -> Unit,
    onRun: (script: Script) -> Unit
) {
    Scaffold {
        Box(Modifier.padding(it)) {
            Column(
                Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Header()

                if (scripts.isNotEmpty()) scripts.forEach { script ->
                    ItemScript(
                        script = script,
                        onOpen = onOpen,
                        onRename = onRename,
                        onDelete = onDelete,
                        onRun = onRun
                    )
                } else Text(
                    text = "No scripts saved yet!",
                    style = MaterialTheme.typography.labelLarge
                )

            }
        }
    }
}

@Composable
private fun Header() {
    Column {

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(id = R.string.home_scripts),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(id = R.color.dark_blue))
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ItemScript(
    script: Script,
    onOpen: (script: Script) -> Unit,
    onDelete: (script: Script) -> Unit,
    onRename: (script: Script) -> Unit,
    onRun: (script: Script) -> Unit
) {
    Row(
        Modifier
            .background(
                color = grey100,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onOpen.invoke(script) },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = script.name,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )
        if (script.isPython) Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "",
            tint = Color.Red,
            modifier = Modifier.clickable { onRun.invoke(script) }
        )
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "",
            tint = Color.Black,
            modifier = Modifier.clickable { onRename.invoke(script) }
        )
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "",
            tint = Color.Black,
            modifier = Modifier.clickable { onDelete.invoke(script) }
        )
    }
}