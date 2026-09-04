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

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPolishPrimary,
    onPrimary = DarkPolishOnPrimary,
    primaryContainer = DarkPolishPrimaryContainer,
    onPrimaryContainer = DarkPolishOnPrimaryContainer,
    secondary = DarkPolishSecondary,
    onSecondary = DarkPolishOnSecondary,
    secondaryContainer = DarkPolishSecondaryContainer,
    onSecondaryContainer = DarkPolishOnSecondaryContainer,
    tertiary = DarkPolishTertiary,
    onTertiary = DarkPolishOnTertiary,
    tertiaryContainer = DarkPolishTertiaryContainer,
    onTertiaryContainer = DarkPolishOnTertiaryContainer,
    background = DarkPolishBackground,
    onBackground = DarkPolishOnBackground,
    surface = DarkPolishSurface,
    onSurface = DarkPolishOnSurface,
    surfaceVariant = DarkPolishSurfaceVariant,
    onSurfaceVariant = DarkPolishOnSurfaceVariant,
    outline = DarkPolishOutline,
    error = PolishError,
    onError = PolishOnError,
    errorContainer = PolishErrorContainer,
    onErrorContainer = PolishOnErrorContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PolishPrimary,
    onPrimary = PolishOnPrimary,
    primaryContainer = PolishPrimaryContainer,
    onPrimaryContainer = PolishOnPrimaryContainer,
    secondary = PolishSecondary,
    onSecondary = PolishOnSecondary,
    secondaryContainer = PolishSecondaryContainer,
    onSecondaryContainer = PolishOnSecondaryContainer,
    tertiary = PolishTertiary,
    onTertiary = PolishOnTertiary,
    tertiaryContainer = PolishTertiaryContainer,
    onTertiaryContainer = PolishOnTertiaryContainer,
    background = PolishBackground,
    onBackground = PolishOnBackground,
    surface = PolishSurface,
    onSurface = PolishOnSurface,
    surfaceVariant = PolishSurfaceVariant,
    onSurfaceVariant = PolishOnSurfaceVariant,
    outline = PolishOutline,
    error = PolishError,
    onError = PolishOnError,
    errorContainer = PolishErrorContainer,
    onErrorContainer = PolishOnErrorContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep branded financial styling consistent
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
