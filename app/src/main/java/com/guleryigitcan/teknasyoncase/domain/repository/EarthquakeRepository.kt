package com.guleryigitcan.teknasyoncase.domain.repository

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface EarthquakeRepository {
    fun observeEarthquakes(): Flow<List<Earthquake>>

    suspend fun refreshEarthquakes(): Result<Unit>

    suspend fun refreshAndGetRecentEarthquakes(
        minimumMagnitude: Double,
        maxResults: Int,
    ): Result<List<Earthquake>>

    suspend fun getEarthquake(id: String): Earthquake?
}
