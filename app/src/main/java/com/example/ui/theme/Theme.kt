package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ChoirGoldLight,
    onPrimary = ChoirNavyDark,
    primaryContainer = ChoirNavyLight,
    onPrimaryContainer = ChoirGoldLight,
    secondary = ChoirGoldAmber,
    onSecondary = ChoirNavyDark,
    tertiary = ChoirBlueAccent,
    background = ChoirBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = ChoirSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = ChoirNavyLight,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = ChoirNavy,
    onPrimary = Color.White,
    primaryContainer = ChoirNavyLight,
    onPrimaryContainer = ChoirGoldLight,
    secondary = ChoirGold,
    onSecondary = Color.White,
    secondaryContainer = ChoirGoldSurface,
    onSecondaryContainer = ChoirNavyDark,
    tertiary = ChoirRoyalBlue,
    background = ChoirBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = ChoirSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = ChoirCardBorder
)

@Composable
fun SionChoirTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
