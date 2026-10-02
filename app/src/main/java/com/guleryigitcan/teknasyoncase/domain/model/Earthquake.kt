package com.guleryigitcan.teknasyoncase.domain.model

data class Earthquake(
    val id: String,
    val magnitude: Double,
    val place: String,
    val timestampMillis: Long,
    val longitude: Double,
    val latitude: Double,
    val depthKm: Double?,
    val usgsUrl: String?,
)
