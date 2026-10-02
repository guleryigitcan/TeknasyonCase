package com.guleryigitcan.teknasyoncase.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = Color.Accent,
    onPrimary = Color.Surface,
    primaryContainer = Color.PrimaryContainer,
    onPrimaryContainer = Color.Accent,
    secondaryContainer = Color.StrongContainer,
    onSecondaryContainer = Color.StrongText,
    tertiaryContainer = Color.ModerateContainer,
    onTertiaryContainer = Color.ModerateText,
    errorContainer = Color.SevereContainer,
    onErrorContainer = Color.SevereText,
    background = Color.Background,
    onBackground = Color.PrimaryText,
    surface = Color.Surface,
    onSurface = Color.PrimaryText,
    surfaceVariant = Color.Background,
    onSurfaceVariant = Color.MutedText,
    outline = Color.MutedText,
    outlineVariant = Color.Divider,
)

@Composable
fun TeknasyonCaseTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = AppColorScheme, typography = Typography, content = content)
}
