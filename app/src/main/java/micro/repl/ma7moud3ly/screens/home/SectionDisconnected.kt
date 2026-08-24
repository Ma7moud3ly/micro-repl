/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.NewHomeTheme

@Preview
@Composable
private fun SectionDisconnectedPreview() {
    NewHomeTheme(darkTheme = true) {
        SectionDisconnected(uiEvents = {})
    }
}

@Preview
@Composable
private fun SectionDisconnectedPreviewLight() {
    NewHomeTheme(darkTheme = false) {
        SectionDisconnected(uiEvents = {})
    }
}

@Composable
internal fun SectionDisconnected(
    uiEvents: (HomeEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        EmptyStateCard(
            onConnect = { uiEvents(HomeEvents.Connect) },
            onRestart = { uiEvents(HomeEvents.RestartApp) }
        )
        Workspace(connected = false, uiEvents = uiEvents)
    }
}

@Composable
private fun EmptyStateCard(
    onConnect: () -> Unit,
    onRestart: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .dashedBorder()
            .padding(horizontal = 22.dp, vertical = 28.dp)
    ) {
        IconBadge(text = ">_", size = 36.dp)
        Text(
            text = stringResource(R.string.home_no_device),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.home_no_device_msg),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.5.sp,
            lineHeight = 21.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        )
        Button(
            onClick = onConnect,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = stringResource(R.string.home_connect_device),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        OutlinedButton(
            onClick = onRestart,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = stringResource(R.string.home_restart),
                fontSize = 14.sp
            )
        }
    }
}

/** A 1dp dashed outline in the theme's [outline] colour. */
@Composable
private fun Modifier.dashedBorder(): Modifier {
    val color = MaterialTheme.colorScheme.outline
    return this.drawBehind {
        val stroke = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(6.dp.toPx(), 5.dp.toPx()), 0f
            )
        )
        val radius = 14.dp.toPx()
        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
        )
    }
}
