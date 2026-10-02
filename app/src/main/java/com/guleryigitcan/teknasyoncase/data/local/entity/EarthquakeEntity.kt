package com.guleryigitcan.teknasyoncase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earthquakes")
internal data class EarthquakeEntity(
    @PrimaryKey val id: String,
    val magnitude: Double,
    val place: String,
    val timestampMillis: Long,
    val longitude: Double,
    val latitude: Double,
    val depthKm: Double?,
    val usgsUrl: String?,
)
