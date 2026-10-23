package com.whaleradar.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DarkBackground,
    secondary = GreenBuy,
    onSecondary = DarkBackground,
    tertiary = AmberWhale,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = RedSell,
    onError = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = CyanAccent,
    onPrimary = LightSurface,
    secondary = GreenBuy,
    onSecondary = LightSurface,
    tertiary = AmberWhale,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = RedSell,
    onError = LightSurface
)

@Composable
fun WhaleRadarTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
