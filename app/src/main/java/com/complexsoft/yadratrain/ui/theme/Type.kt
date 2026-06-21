package com.complexsoft.yadratrain.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.complexsoft.yadratrain.R

// Requiere los .ttf en res/font/. Si no los tienes aún, ver nota al final.
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
 NOTA: descarga JetBrains Mono (gratis, OFL) de https://www.jetbrains.com/lp/mono/
 y coloca estos 3 archivos en app/src/main/res/font/:
   jetbrains_mono_regular.ttf
   jetbrains_mono_medium.ttf
   jetbrains_mono_bold.ttf
 Si por ahora no quieres bajar las fuentes, reemplaza `FontFamily.Monospace` por
 `FontFamily.Monospace` (la mono del sistema) y todo compila igual,
 solo se ve un poco menos distintivo.
*/