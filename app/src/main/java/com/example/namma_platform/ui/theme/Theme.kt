package com.example.namma_platform.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = SecondaryBlue,
    onPrimaryContainer = SurfaceWhite,
    secondary = AccentYellow,
    onSecondary = TextDark,
    background = BackgroundWhite,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun NammaPlatformTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
