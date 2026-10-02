package com.guleryigitcan.teknasyoncase.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EarthquakeResponseDto(
    val features: List<EarthquakeFeatureDto>,
)

@Serializable
internal data class EarthquakeFeatureDto(
    val id: String? = null,
    val properties: EarthquakePropertiesDto? = null,
    val geometry: EarthquakeGeometryDto? = null,
)

@Serializable
internal data class EarthquakePropertiesDto(
    @SerialName("mag") val magnitude: Double? = null,
    val place: String? = null,
    val time: Long? = null,
    val url: String? = null,
)

@Serializable
internal data class EarthquakeGeometryDto(
    val coordinates: List<Double?>? = null,
)
