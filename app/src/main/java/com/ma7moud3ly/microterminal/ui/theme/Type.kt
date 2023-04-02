package com.ma7moud3ly.microterminal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.microterminal.R

val fontConsolas = FontFamily(
    Font(R.font.consolas, FontWeight.Normal),
    Font(R.font.consolas_bold, FontWeight.Bold)
)

private val fontRoboto = FontFamily(
    Font(R.font.roboto, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
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
val Typography = Typography(
    bodyLarge = bodyLarge.copy(fontFamily = fontRoboto),
    bodyMedium = bodyMedium.copy(fontFamily = fontRoboto),
    titleLarge = titleLarge.copy(fontFamily = fontRoboto),
    titleMedium = titleMedium.copy(fontFamily = fontRoboto),
    titleSmall = titleSmall.copy(fontFamily = fontRoboto),
    labelSmall = labelSmall.copy(fontFamily = fontRoboto),
    labelMedium = labelMedium.copy(fontFamily = fontRoboto)
)