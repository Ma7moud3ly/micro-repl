package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MyScreen(
    modifier: Modifier = Modifier.padding(16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacedBy: Dp = 8.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(spacedBy),
    background: Color = MaterialTheme.colorScheme.background,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = header,
        bottomBar = footer,
        containerColor = background
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                content = content
            )
        }
    }
}