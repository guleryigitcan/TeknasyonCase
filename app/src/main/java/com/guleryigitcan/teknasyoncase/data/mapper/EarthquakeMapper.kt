package com.guleryigitcan.teknasyoncase.data.mapper

import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeFeatureDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeResponseDto
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake

internal fun EarthquakeResponseDto.toDomain(): List<Earthquake> = features.mapNotNull { it.toDomain() }

internal fun EarthquakeFeatureDto.toDomain(): Earthquake? {
    val earthquakeId = id?.takeIf { it.isNotBlank() } ?: return null
    val details = properties ?: return null
    val magnitude = details.magnitude?.takeIf { it.isFinite() } ?: return null
    val timestamp = details.time?.takeIf { it >= 0 } ?: return null
    val coordinates = geometry?.coordinates ?: return null
    val longitude = coordinates.getOrNull(0)?.takeIf { it.isFinite() && it in -180.0..180.0 }
        ?: return null
    val latitude = coordinates.getOrNull(1)?.takeIf { it.isFinite() && it in -90.0..90.0 }
        ?: return null
    val depth = coordinates.getOrNull(2)?.takeIf { it.isFinite() }

    return Earthquake(
        id = earthquakeId,
        magnitude = magnitude,
        place = details.place?.takeIf { it.isNotBlank() } ?: "Unknown location",
        timestampMillis = timestamp,
        longitude = longitude,
        latitude = latitude,
        depthKm = depth,
        usgsUrl = details.url?.takeIf { it.isNotBlank() },
    )
}
