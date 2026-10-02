package com.guleryigitcan.teknasyoncase.presentation.home.model

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import com.guleryigitcan.teknasyoncase.presentation.model.toEarthquakeSeverity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class EarthquakeUiModel(
    val id: String,
    val magnitudeText: String,
    val place: String,
    val timeText: String,
    val depthText: String?,
    val severity: EarthquakeSeverity,
)

internal fun Earthquake.toUiModel(
    locale: Locale = Locale.getDefault(),
    timeZone: TimeZone = TimeZone.getDefault(),
): EarthquakeUiModel {
    val timeFormat = SimpleDateFormat("d MMM, HH:mm", locale).apply { this.timeZone = timeZone }
    return EarthquakeUiModel(
        id = id,
        magnitudeText = String.format(locale, "%.1f", magnitude),
        place = place,
        timeText = timeFormat.format(Date(timestampMillis)),
        depthText = depthKm?.let { String.format(locale, "%.1f", it) },
        severity = magnitude.toEarthquakeSeverity(),
    )
}
