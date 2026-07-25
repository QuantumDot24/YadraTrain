package com.complexsoft.yadratrain.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.complexsoft.yadratrain.R

// Requires .ttf files in res/font/. If you don't have them yet, see note at the end.
//val FontFamily.Monospace = FontFamily(
//    Font(R.font.etbrains_mono_regular, FontWeight.Normal),
//    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
//    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
//)

val YadraTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = 2.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp
    )
)

/*
 NOTE: Download JetBrains Mono (free, OFL) from https://www.jetbrains.com/lp/mono/
 and place these 3 files in app/src/main/res/font/:
   jetbrains_mono_regular.ttf
   jetbrains_mono_medium.ttf
   jetbrains_mono_bold.ttf
 If you don't want to download the fonts yet, replace `FontFamily.Monospace` with
 `FontFamily.Monospace` (system mono) and everything will compile,
 it will just look slightly less distinctive.
*/