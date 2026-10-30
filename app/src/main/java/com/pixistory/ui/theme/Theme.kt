package com.pixistory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colours taken from the Pixistory logo: blue, green, amber and red on a deep navy.
private val Light = lightColorScheme(
    primary = Color(0xFF1A73E8), onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E3FC), onPrimaryContainer = Color(0xFF0B2A5B),
    secondary = Color(0xFF188038), onSecondary = Color.White,
    secondaryContainer = Color(0xFFCEEAD6), onSecondaryContainer = Color(0xFF0B3D1D),
    tertiary = Color(0xFFE37400), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEEFC3), onTertiaryContainer = Color(0xFF5A3B00),
    error = Color(0xFFD93025), onError = Color.White,
    errorContainer = Color(0xFFFAD2CF), onErrorContainer = Color(0xFF5C0F0A),
    background = Color(0xFFF4F8FF), onBackground = Color(0xFF14202E),
    surface = Color(0xFFF4F8FF), onSurface = Color(0xFF14202E),
    surfaceVariant = Color(0xFFDDE7F6), onSurfaceVariant = Color(0xFF45546A),
    outline = Color(0xFF7A8AA3), outlineVariant = Color(0xFFC3D2E8),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFEAF1FD),
    surfaceContainer = Color(0xFFE3ECFB), surfaceContainerHigh = Color(0xFFDBE6F8),
    surfaceContainerHighest = Color(0xFFD3E0F5),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF8AB4F8), onPrimary = Color(0xFF062E6F),
    primaryContainer = Color(0xFF1F3E73), onPrimaryContainer = Color(0xFFD2E3FC),
    secondary = Color(0xFF81C995), onSecondary = Color(0xFF0B3D1D),
    secondaryContainer = Color(0xFF174D2B), onSecondaryContainer = Color(0xFFCEEAD6),
    tertiary = Color(0xFFFDD663), onTertiary = Color(0xFF3F2E00),
    tertiaryContainer = Color(0xFF574100), onTertiaryContainer = Color(0xFFFEEFC3),
    error = Color(0xFFF28B82), onError = Color(0xFF5C0F0A),
    errorContainer = Color(0xFF7A1F19), onErrorContainer = Color(0xFFFAD2CF),
    background = Color(0xFF0E1726), onBackground = Color(0xFFE3EAF6),
    surface = Color(0xFF0E1726), onSurface = Color(0xFFE3EAF6),
    surfaceVariant = Color(0xFF2A3850), onSurfaceVariant = Color(0xFFB4C2D9),
    outline = Color(0xFF7F90AB), outlineVariant = Color(0xFF34455F),
    surfaceContainerLowest = Color(0xFF09111D), surfaceContainerLow = Color(0xFF142034),
    surfaceContainer = Color(0xFF18263D), surfaceContainerHigh = Color(0xFF1F2F49),
    surfaceContainerHighest = Color(0xFF273A58),
)

@Composable
fun PixistoryTheme(mode: String, content: @Composable () -> Unit) {
    val dark = when (mode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
