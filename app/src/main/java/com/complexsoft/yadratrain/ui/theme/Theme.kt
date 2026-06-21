package com.complexsoft.yadratrain.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val YadraDarkScheme = darkColorScheme(
    background = YadraBg,
    surface = YadraSurface,
    surfaceVariant = YadraSurfaceRaised,
    onBackground = YadraTextPrimary,
    onSurface = YadraTextPrimary,
    onSurfaceVariant = YadraTextDim,
    primary = YadraStructural,
    onPrimary = YadraBg,
    secondary = YadraAccuracy,
    onSecondary = YadraBg,
    tertiary = YadraLoss,
    onTertiary = YadraBg,
    error = YadraError,
    onError = YadraBg,
    outline = YadraBorder,
    outlineVariant = YadraTextFaint
)

@Composable
fun YadraTrainTheme(
    // El tema es siempre oscuro a propósito — es la identidad de la app,
    // no una preferencia del sistema. Se ignora isSystemInDarkTheme().
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = YadraDarkScheme,
        typography = YadraTypography,
        content = content
    )
}