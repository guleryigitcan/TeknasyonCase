package com.guleryigitcan.teknasyoncase.data.repository

import com.guleryigitcan.teknasyoncase.data.local.dao.EarthquakeDao
import com.guleryigitcan.teknasyoncase.data.mapper.toDomain
import com.guleryigitcan.teknasyoncase.data.mapper.toEntity
import com.guleryigitcan.teknasyoncase.data.remote.api.UsgsEarthquakeApi
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeResponseDto
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class EarthquakeRepositoryImpl @Inject constructor(
    private val api: UsgsEarthquakeApi,
    private val dao: EarthquakeDao,
) : EarthquakeRepository {
    override fun observeEarthquakes(): Flow<List<Earthquake>> =
        dao.observeEarthquakes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshEarthquakes(): Result<Unit> =
        fetchAndPersist(updateHomeFeed = true) { api.getEarthquakes() }.map { Unit }

    override suspend fun refreshAndGetRecentEarthquakes(
        minimumMagnitude: Double,
        maxResults: Int,
    ): Result<List<Earthquake>> = fetchAndPersist(updateHomeFeed = false) {
        api.getEarthquakes(
            minMagnitude = minimumMagnitude,
            limit = maxResults,
        )
    }

    private suspend fun fetchAndPersist(
        updateHomeFeed: Boolean,
        fetch: suspend () -> EarthquakeResponseDto,
    ): Result<List<Earthquake>> = try {
        val response = fetch()
        val earthquakes = response.toDomain()
        check(response.features.isEmpty() || earthquakes.isNotEmpty()) {
            "USGS response had features but none were usable"
        }
        val entities = earthquakes.map { it.toEntity() }
        if (updateHomeFeed) dao.replaceHomeFeed(entities) else dao.upsertEarthquakes(entities)
        Result.success(earthquakes)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: Exception) {
        Result.failure(failure)
    }

    override suspend fun getEarthquake(id: String): Earthquake? = dao.getEarthquake(id)?.toDomain()
}
