package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    secondary = BlueSecondary,
    tertiary = CyanAccent,
    background = CosmicBackground,
    surface = CosmicSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),     // Beautiful light blue (Sky 600)
    secondary = Color(0xFF38BDF8),   // Bright light blue (Sky 400)
    tertiary = Color(0xFF0EA5E9),    // Sky 500
    background = Color(0xFFF0F9FF),  // Sky 50 - clean light ice/sky blue
    surface = Color(0xFFFFFFFF),     // Pure white surfaces
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A), // Slate 900 (highly readable dark text)
    onSurface = Color(0xFF1E293B)     // Slate 800
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val targetColors = if (darkTheme) DarkColorScheme else LightColorScheme

    val primaryState = animateColorAsState(targetColors.primary, tween(durationMillis = 600), label = "primary")
    val secondaryState = animateColorAsState(targetColors.secondary, tween(durationMillis = 600), label = "secondary")
    val tertiaryState = animateColorAsState(targetColors.tertiary, tween(durationMillis = 600), label = "tertiary")
    val backgroundState = animateColorAsState(targetColors.background, tween(durationMillis = 600), label = "background")
    val surfaceState = animateColorAsState(targetColors.surface, tween(durationMillis = 600), label = "surface")
    val onPrimaryState = animateColorAsState(targetColors.onPrimary, tween(durationMillis = 600), label = "onPrimary")
    val onSecondaryState = animateColorAsState(targetColors.onSecondary, tween(durationMillis = 600), label = "onSecondary")
    val onBackgroundState = animateColorAsState(targetColors.onBackground, tween(durationMillis = 600), label = "onBackground")
    val onSurfaceState = animateColorAsState(targetColors.onSurface, tween(durationMillis = 600), label = "onSurface")

    val colors = if (darkTheme) {
        darkColorScheme(
            primary = primaryState.value,
            secondary = secondaryState.value,
            tertiary = tertiaryState.value,
            background = backgroundState.value,
            surface = surfaceState.value,
            onPrimary = onPrimaryState.value,
            onSecondary = onSecondaryState.value,
            onBackground = onBackgroundState.value,
            onSurface = onSurfaceState.value
        )
    } else {
        lightColorScheme(
            primary = primaryState.value,
            secondary = secondaryState.value,
            tertiary = tertiaryState.value,
            background = backgroundState.value,
            surface = surfaceState.value,
            onPrimary = onPrimaryState.value,
            onSecondary = onSecondaryState.value,
            onBackground = onBackgroundState.value,
            onSurface = onSurfaceState.value
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
