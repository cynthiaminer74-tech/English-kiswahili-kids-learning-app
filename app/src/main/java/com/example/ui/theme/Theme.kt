package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = SavannaPrimary,
    onPrimary = SavannaOnPrimary,
    primaryContainer = SavannaPrimaryContainer,
    onPrimaryContainer = SavannaOnPrimaryContainer,
    secondary = SafariSecondary,
    onSecondary = SafariOnSecondary,
    secondaryContainer = SafariSecondaryContainer,
    onSecondaryContainer = SafariOnSecondaryContainer,
    tertiary = SunOrangeTertiary,
    onTertiary = SunOrangeOnTertiary,
    tertiaryContainer = SunOrangeTertiaryContainer,
    onTertiaryContainer = SunOrangeOnTertiaryContainer,
    background = AppBackground,
    onBackground = AppOnBackground,
    surface = AppSurface,
    onSurface = AppOnSurface,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppOnSurfaceVariant,
  )

private val DarkColorScheme = LightColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = LightColorScheme, typography = Typography, content = content)
}
