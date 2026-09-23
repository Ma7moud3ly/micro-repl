package micro.repl.ma7moud3ly.feature.terminal

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun TextUnit.zoomIn(): TextUnit {
    return if (this.value <= 25) (this.value + 4).sp
    else this
}

fun TextUnit.zoomOut(): TextUnit {
    return if (this.value >= 11) (this.value - 4).sp
    else this
}