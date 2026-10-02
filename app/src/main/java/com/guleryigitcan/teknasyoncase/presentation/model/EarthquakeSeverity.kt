package com.guleryigitcan.teknasyoncase.presentation.model

enum class EarthquakeSeverity { MINOR, MODERATE, STRONG, SEVERE }

internal fun Double.toEarthquakeSeverity(): EarthquakeSeverity = when {
    this >= 5.0 -> EarthquakeSeverity.SEVERE
    this >= 4.0 -> EarthquakeSeverity.STRONG
    this >= 3.0 -> EarthquakeSeverity.MODERATE
    else -> EarthquakeSeverity.MINOR
}
