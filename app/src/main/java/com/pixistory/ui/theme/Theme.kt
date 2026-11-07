package com.pixistory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Design language: calm and editorial. Neutral cool-grey surfaces, one restrained blue accent, colour used only to
 * mean something (status), thin hairlines instead of heavy boxes, generous spacing, large quiet titles.
 */
private val Ink = Color(0xFF0F1419)

private val Light = lightColorScheme(
    primary = Color(0xFF1A5FD0), onPrimary = Color.White,
    primaryContainer = Color(0xFFE6EEFC), onPrimaryContainer = Color(0xFF0B2F6B),
    secondary = Color(0xFF2E7D5B), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6F1EC), onSecondaryContainer = Color(0xFF123D2B),
    tertiary = Color(0xFFB26A00), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFBEFD9), onTertiaryContainer = Color(0xFF4A2D00),
    error = Color(0xFFC0392B), onError = Color.White,
    errorContainer = Color(0xFFFBE6E3), onErrorContainer = Color(0xFF5A1610),
    background = Color(0xFFF7F8FA), onBackground = Ink,
    surface = Color(0xFFF7F8FA), onSurface = Ink,
    surfaceVariant = Color(0xFFEDEFF3), onSurfaceVariant = Color(0xFF5A6472),
    outline = Color(0xFF8B94A1), outlineVariant = Color(0xFFE2E5EA),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF0F2F5), surfaceContainerHigh = Color(0xFFEBEDF1),
    surfaceContainerHighest = Color(0xFFE5E8ED),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF8DB4F7), onPrimary = Color(0xFF06214F),
    primaryContainer = Color(0xFF1B2F55), onPrimaryContainer = Color(0xFFD9E5FB),
    secondary = Color(0xFF7FCBA7), onSecondary = Color(0xFF0B2E1F),
    secondaryContainer = Color(0xFF173528), onSecondaryContainer = Color(0xFFD3EDE0),
    tertiary = Color(0xFFE5B866), onTertiary = Color(0xFF3A2400),
    tertiaryContainer = Color(0xFF3D2C0E), onTertiaryContainer = Color(0xFFF7E6C4),
    error = Color(0xFFF0958A), onError = Color(0xFF51120B),
    errorContainer = Color(0xFF5A211B), onErrorContainer = Color(0xFFFBDAD6),
    background = Color(0xFF0C1016), onBackground = Color(0xFFE6EAF0),
    surface = Color(0xFF0C1016), onSurface = Color(0xFFE6EAF0),
    surfaceVariant = Color(0xFF232B36), onSurfaceVariant = Color(0xFFA6B0BF),
    outline = Color(0xFF6F7A8A), outlineVariant = Color(0xFF262F3B),
    surfaceContainerLowest = Color(0xFF080B10), surfaceContainerLow = Color(0xFF141A22),
    surfaceContainer = Color(0xFF171E27), surfaceContainerHigh = Color(0xFF1C242F),
    surfaceContainerHighest = Color(0xFF222B37),
)

private val Sans = FontFamily.SansSerif

private val PixiTypography = Typography(
    displaySmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.4).sp),
    headlineMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.3).sp),
    headlineSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.2).sp),
    titleLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.1).sp),
    titleMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 12.5.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp),
    labelSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.2.sp),
)

private val PixiShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
)

@Composable
fun PixistoryTheme(mode: String, content: @Composable () -> Unit) {
    val dark = when (mode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) Dark else Light,
        typography = PixiTypography,
        shapes = PixiShapes,
        content = content,
    )
}
