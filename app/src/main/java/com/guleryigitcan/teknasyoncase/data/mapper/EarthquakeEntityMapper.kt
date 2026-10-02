package com.guleryigitcan.teknasyoncase.data.mapper

import com.guleryigitcan.teknasyoncase.data.local.entity.EarthquakeEntity
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake

internal fun EarthquakeEntity.toDomain(): Earthquake = Earthquake(
    id = id,
    magnitude = magnitude,
    place = place,
    timestampMillis = timestampMillis,
    longitude = longitude,
    latitude = latitude,
    depthKm = depthKm,
    usgsUrl = usgsUrl,
)

internal fun Earthquake.toEntity(): EarthquakeEntity = EarthquakeEntity(
    id = id,
    magnitude = magnitude,
    place = place,
    timestampMillis = timestampMillis,
    longitude = longitude,
    latitude = latitude,
    depthKm = depthKm,
    usgsUrl = usgsUrl,
)
