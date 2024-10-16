package micro.repl.ma7moud3ly.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
private fun SectionErrorPreview() {
    AppTheme {
        Column {
            SectionError(
                status = TestStatus.error,
                uiEvents = {}
            )
        }
    }
}


@Composable
fun SectionError(
    status: ConnectionStatus.Error,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        DeviceNotConnected(
            onClick = { uiEvents(HomeEvents.FindDevices) }
        )
        HomeButtons(
            isConnected = false,
            uiEvents = uiEvents
        )
    }
}

@Composable
fun DeviceNotConnected(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Red.copy(alpha = 0.2f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                Button(onClick = onClick) {
                    Text(text = stringResource(id = R.string.home_connect))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.home_disconnected),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(shape = CircleShape)
                        .background(color = Color.Red)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.connection),
                contentDescription = "", modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.home_connection_msg),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Justify
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
