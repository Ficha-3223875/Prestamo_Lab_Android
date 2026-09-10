package com.example.prestamolab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo10,
    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = Teal80,
    onSecondaryContainer = Color(0xFF00332E),
    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = Amber80,
    onTertiaryContainer = Color(0xFF3E2700),
    background = SurfaceLight,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo10,
    primaryContainer = Indigo40,
    onPrimaryContainer = Indigo90,
    secondary = Teal80,
    onSecondary = Color(0xFF00332E),
    secondaryContainer = Teal40,
    onSecondaryContainer = Teal80,
    tertiary = Amber80,
    onTertiary = Color(0xFF3E2700),
    tertiaryContainer = Amber40,
    onTertiaryContainer = Amber80,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun PrestamoLabTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
