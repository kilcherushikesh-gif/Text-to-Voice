package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SaffronPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = SaffronPrimary,
    onPrimaryContainer = Color.White,
    secondary = AmberSecondaryLight,
    onSecondary = Color.Black,
    secondaryContainer = AmberSecondaryDark,
    onSecondaryContainer = AmberSecondaryLight,
    tertiary = PeacockTealLight,
    onTertiary = Color.Black,
    tertiaryContainer = PeacockTealDark,
    onTertiaryContainer = PeacockTealLight,
    background = DeepNightBackground,
    onBackground = DeepNightOnSurface,
    surface = DeepNightSurface,
    onSurface = DeepNightOnSurface,
    surfaceVariant = DeepNightSurfaceVariant,
    onSurfaceVariant = DeepNightOnSurface,
    outline = DeepNightOutline
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = SaffronPrimaryDark,
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF4E2600),
    tertiary = PeacockTeal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2DFDB),
    onTertiaryContainer = PeacockTealDark,
    background = WarmCreamBackground,
    onBackground = WarmOnSurface,
    surface = WarmSurface,
    onSurface = WarmOnSurface,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = WarmOnSurface,
    outline = WarmOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Preserve custom branding by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
