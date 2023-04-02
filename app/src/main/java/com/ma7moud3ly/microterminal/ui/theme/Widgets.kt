package com.ma7moud3ly.microterminal.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ProgressDialog(
    background: Color = Color.Transparent,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = background)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp),
                color = Color.Green.copy(alpha = 0.2f)
            )
        }

    }
}

@Composable
fun ProgressView() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = Color.Green.copy(alpha = 0.2f)
        )
    }
}
