package com.guleryigitcan.teknasyoncase.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.guleryigitcan.teknasyoncase.R
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import com.guleryigitcan.teknasyoncase.ui.theme.MinorContainer
import com.guleryigitcan.teknasyoncase.ui.theme.MinorText
import com.guleryigitcan.teknasyoncase.ui.theme.ModerateContainer
import com.guleryigitcan.teknasyoncase.ui.theme.ModerateText
import com.guleryigitcan.teknasyoncase.ui.theme.StrongContainer
import com.guleryigitcan.teknasyoncase.ui.theme.StrongText
import com.guleryigitcan.teknasyoncase.ui.theme.SevereContainer
import com.guleryigitcan.teknasyoncase.ui.theme.SevereText

data class SeverityColors(val container: Color, val content: Color)

@Composable
fun severityColors(severity: EarthquakeSeverity): SeverityColors {
    return when (severity) {
        EarthquakeSeverity.MINOR -> SeverityColors(Color.MinorContainer, Color.MinorText)
        EarthquakeSeverity.MODERATE -> SeverityColors(Color.ModerateContainer, Color.ModerateText)
        EarthquakeSeverity.STRONG -> SeverityColors(Color.StrongContainer, Color.StrongText)
        EarthquakeSeverity.SEVERE -> SeverityColors(Color.SevereContainer, Color.SevereText)
    }
}

@Composable
fun severityLabel(severity: EarthquakeSeverity): String = stringResource(
    when (severity) {
        EarthquakeSeverity.MINOR -> R.string.severity_minor
        EarthquakeSeverity.MODERATE -> R.string.severity_moderate
        EarthquakeSeverity.STRONG -> R.string.severity_strong
        EarthquakeSeverity.SEVERE -> R.string.severity_severe
    },
)
