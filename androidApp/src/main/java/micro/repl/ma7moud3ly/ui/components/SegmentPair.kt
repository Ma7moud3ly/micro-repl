package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

@Composable
fun SegmentPair(
    cellWidth: Dp,
    cellHeight: Dp,
    onStart: () -> Unit,
    onEnd: () -> Unit,
    start: @Composable () -> Unit,
    end: @Composable () -> Unit,
    startEnabled: Boolean = true,
    endEnabled: Boolean = true
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.height(cellHeight)) {
            SegmentCell(cellWidth, cellHeight, startEnabled, onStart, start)
            VerticalDivider(
                modifier = Modifier.height(cellHeight),
                color = MaterialTheme.colorScheme.outline
            )
            SegmentCell(cellWidth, cellHeight, endEnabled, onEnd, end)
        }
    }
}

@Composable
fun SegmentCell(
    width: Dp,
    height: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun SegmentLabel(text: String, color: Color) {
    Text(
        text = text,
        fontFamily = fontConsolas,
        fontSize = 11.sp,
        color = color
    )
}

@Composable
fun SegmentIcon(icon: Int, tint: Color) {
    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(14.dp)
    )
}