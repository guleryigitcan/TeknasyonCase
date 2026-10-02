package com.guleryigitcan.teknasyoncase.presentation.detail.model

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import com.guleryigitcan.teknasyoncase.presentation.model.toEarthquakeSeverity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class EarthquakeDetailUiModel(
    val magnitudeText: String,
    val place: String,
    val occurredText: String,
    val depthText: String?,
    val latitudeText: String,
    val longitudeText: String,
    val severity: EarthquakeSeverity,
)

internal fun Earthquake.toDetailUiModel(
    locale: Locale = Locale.getDefault(),
    timeZone: TimeZone = TimeZone.getDefault(),
): EarthquakeDetailUiModel {
    val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", locale).apply {
        this.timeZone = timeZone
    }
    return EarthquakeDetailUiModel(
        magnitudeText = String.format(locale, "%.1f", magnitude),
        place = place,
        occurredText = dateFormat.format(Date(timestampMillis)),
        depthText = depthKm?.let { String.format(locale, "%.1f km", it) },
        latitudeText = String.format(locale, "%.3f°", latitude),
        longitudeText = String.format(locale, "%.3f°", longitude),
        severity = magnitude.toEarthquakeSeverity(),
    )
}
