package com.android.app.socialnestapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = SocialNestPrimary,
    onPrimary = SocialNestBackground,
    primaryContainer = SocialNestPrimaryContainer,
    onPrimaryContainer = SocialNestOnPrimaryContainer,
    secondary = SocialNestSecondary,
    onSecondary = SocialNestBackground,
    secondaryContainer = SocialNestSecondaryContainer,
    onSecondaryContainer = SocialNestOnSecondaryContainer,
    tertiary = SocialNestAccent,
    onTertiary = SocialNestText,
    tertiaryContainer = SocialNestAccentContainer,
    onTertiaryContainer = SocialNestOnAccentContainer,
    background = SocialNestBackground,
    onBackground = SocialNestText,
    surface = SocialNestSurface,
    onSurface = SocialNestText,
    surfaceVariant = SocialNestPrimaryContainer,
    onSurfaceVariant = SocialNestMutedText,
    outline = SocialNestOutline,
    error = SocialNestError,
    onError = SocialNestSurface
)

private val DarkColors = darkColorScheme(
    primary = SocialNestPrimaryDark,
    onPrimary = SocialNestPrimary,
    primaryContainer = SocialNestPrimary,
    onPrimaryContainer = SocialNestPrimaryContainer,
    secondary = SocialNestSecondaryDark,
    onSecondary = SocialNestPrimary,
    secondaryContainer = SocialNestSecondary,
    onSecondaryContainer = SocialNestSecondaryContainer,
    tertiary = SocialNestAccent,
    onTertiary = SocialNestText,
    tertiaryContainer = SocialNestOnAccentContainer,
    onTertiaryContainer = SocialNestAccentContainer,
    background = SocialNestBackgroundDark,
    onBackground = SocialNestTextDark,
    surface = SocialNestSurfaceDark,
    onSurface = SocialNestTextDark,
    surfaceVariant = Color(0xFF1C3F3C),
    onSurfaceVariant = Color(0xFFCCFBF1),
    outline = Color(0xFF3D5C59),
    error = SocialNestError,
    onError = SocialNestText
)

private val SocialNestShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun SocialNestApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SocialNestTypography,
            shapes = SocialNestShapes,
            content = content
        )
    }
}