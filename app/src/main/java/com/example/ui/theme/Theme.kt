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
    primary = SocialBlue,
    onPrimary = Color.White,
    secondary = SocialPink,
    onSecondary = Color.White,
    tertiary = SocialOrange,
    background = Color(0xFF0F0F12),
    surface = Color(0xFF18181D),
    onBackground = Color(0xFFF0F0F0),
    onSurface = Color(0xFFF0F0F0),
    outline = Color(0xFF2C2C34),
    surfaceVariant = Color(0xFF23232A),
    onSurfaceVariant = Color(0xFFAAAAAA)
)

private val LightColorScheme = lightColorScheme(
    primary = SocialBlue,
    onPrimary = Color.White,
    secondary = SocialPink,
    onSecondary = Color.White,
    tertiary = SocialOrange,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderLight,
    surfaceVariant = Color(0xFFF0F0F2),
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional branded social styling
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
