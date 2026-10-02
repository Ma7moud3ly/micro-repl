/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.consolas
import micro.repl.ma7moud3ly.shared.resources.consolas_bold
import micro.repl.ma7moud3ly.shared.resources.f04b03
import micro.repl.ma7moud3ly.shared.resources.roboto
import micro.repl.ma7moud3ly.shared.resources.roboto_bold
import micro.repl.ma7moud3ly.shared.resources.roboto_medium
import org.jetbrains.compose.resources.Font


val fontConsolas: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.consolas, FontWeight.Normal),
        Font(Res.font.consolas_bold, FontWeight.Bold)
    )

val font04b03: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.f04b03, FontWeight.Normal)
    )

private val fontRoboto: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.roboto, FontWeight.Normal),
        Font(Res.font.roboto_medium, FontWeight.Medium),
        Font(Res.font.roboto_bold, FontWeight.Bold)
    )
private val labelLarge = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)
private val labelMedium = labelLarge.copy(
    fontSize = 12.sp,
)

private val labelSmall = labelLarge.copy(
    fontSize = 10.sp,
)

private val titleLarge = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

private val titleMedium = titleLarge.copy(
    fontSize = 18.sp,
)

private val titleSmall = titleLarge.copy(
    fontSize = 16.sp,
)

private val bodyLarge = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    letterSpacing = 0.5.sp
)

private val bodyMedium = bodyLarge.copy(
    fontSize = 14.sp
)

private val bodySmall = bodyLarge.copy(
    fontSize = 12.sp
)

// Set of Material typography styles to start with
val appTypography: Typography
    @Composable get() {
        val font = fontRoboto
        return Typography(
            bodyLarge = bodyLarge.copy(fontFamily = font),
            bodyMedium = bodyMedium.copy(fontFamily = font),
            bodySmall = bodySmall.copy(fontFamily = font),
            titleLarge = titleLarge.copy(fontFamily = font),
            titleMedium = titleMedium.copy(fontFamily = font),
            titleSmall = titleSmall.copy(fontFamily = font),
            labelSmall = labelSmall.copy(fontFamily = font),
            labelMedium = labelMedium.copy(fontFamily = font)
        )
    }