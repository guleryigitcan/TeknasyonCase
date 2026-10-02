package com.guleryigitcan.teknasyoncase.data.repository

import com.guleryigitcan.teknasyoncase.data.local.dao.EarthquakeDao
import com.guleryigitcan.teknasyoncase.data.local.entity.EarthquakeEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.HomeFeedEntity
import com.guleryigitcan.teknasyoncase.data.remote.api.UsgsEarthquakeApi
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeFeatureDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeGeometryDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakePropertiesDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeResponseDto
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EarthquakeRepositoryImplTest {
    @Test
    fun successfulHomeRefreshReplacesVisibleFeedAndKeepsDetailRows() = runBlocking {
        val dao = FakeDao(listOf(entity(id = "cached", timestamp = 1L)))
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.success(response())), dao)

        assertTrue(repository.refreshEarthquakes().isSuccess)
        assertEquals(listOf("us123"), dao.observeEarthquakes().first().map { it.id })
        assertEquals("cached", dao.getEarthquake("cached")?.id)
        assertEquals(12.5, dao.getEarthquake("us123")!!.depthKm!!, 0.0)
    }

    @Test
    fun alertRefreshReturnsOnlyFreshFeedWhileKeepingCache() = runBlocking {
        val dao = FakeDao(listOf(entity(id = "cached", timestamp = 1L)))
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.success(response())), dao)

        assertEquals(
            listOf("us123"),
            repository.refreshAndGetRecentEarthquakes(2.0, 500).getOrThrow().map { it.id },
        )
        assertEquals(listOf("cached"), dao.observeEarthquakes().first().map { it.id })
        assertEquals("us123", dao.getEarthquake("us123")?.id)
    }

    @Test
    fun successfulEmptyHomeResponseClearsVisibleFeedButRetainsDetailRows() = runBlocking {
        val dao = FakeDao(listOf(entity()))
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.success(EarthquakeResponseDto(emptyList()))), dao)

        assertTrue(repository.refreshEarthquakes().isSuccess)
        assertTrue(dao.observeEarthquakes().first().isEmpty())
        assertEquals("cached", dao.getEarthquake("cached")?.id)
    }

    @Test
    fun allMalformedFeaturesFailWithoutClearingCachedHomeFeed() = runBlocking {
        val dao = FakeDao(listOf(entity()))
        val malformed = EarthquakeResponseDto(listOf(EarthquakeFeatureDto(id = "broken")))
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.success(malformed)), dao)

        assertTrue(repository.refreshEarthquakes().isFailure)
        assertEquals(listOf("cached"), dao.observeEarthquakes().first().map { it.id })
        assertTrue(repository.refreshAndGetRecentEarthquakes(2.0, 500).isFailure)
    }

    @Test
    fun failedRefreshPreservesCachedEarthquakes() = runBlocking {
        val cached = entity(id = "cached", timestamp = 1L)
        val dao = FakeDao(listOf(cached))
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.failure(IOException("offline"))), dao)

        assertTrue(repository.refreshEarthquakes().isFailure)
        assertEquals(listOf(cached), dao.observeEarthquakes().first())
    }

    @Test
    fun observeEarthquakesMapsRoomValues() = runBlocking {
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.success(response())), FakeDao(listOf(entity())))

        val earthquake = repository.observeEarthquakes().first().single()
        assertEquals("cached", earthquake.id)
        assertEquals(27.1, earthquake.longitude, 0.0)
        assertEquals(38.4, earthquake.latitude, 0.0)
        assertEquals("https://example.com/cached", earthquake.usgsUrl)
    }

    @Test
    fun getEarthquakeReadsOnlyFromRoom() = runBlocking {
        val repository = EarthquakeRepositoryImpl(FakeApi(Result.failure(IOException("offline"))), FakeDao(listOf(entity())))

        assertEquals("cached", repository.getEarthquake("cached")?.id)
        assertNull(repository.getEarthquake("missing"))
    }

    private fun response() = EarthquakeResponseDto(
        listOf(
            EarthquakeFeatureDto(
                id = "us123",
                properties = EarthquakePropertiesDto(magnitude = 4.2, place = "Near Izmir", time = 2L),
                geometry = EarthquakeGeometryDto(listOf(27.1, 38.4, 12.5)),
            ),
        ),
    )

    private fun entity(id: String = "cached", timestamp: Long = 1L) = EarthquakeEntity(
        id = id,
        magnitude = 3.5,
        place = "Cached place",
        timestampMillis = timestamp,
        longitude = 27.1,
        latitude = 38.4,
        depthKm = null,
        usgsUrl = "https://example.com/cached",
    )

    private class FakeApi(private val response: Result<EarthquakeResponseDto>) : UsgsEarthquakeApi {
        override suspend fun getEarthquakes(
            format: String,
            startTime: String,
            endTime: String?,
            minMagnitude: Double,
            limit: Int,
            orderBy: String,
        ): EarthquakeResponseDto = response.getOrThrow()
    }

    private class FakeDao(initial: List<EarthquakeEntity>) : EarthquakeDao {
        private val rows = initial.associateBy { it.id }.toMutableMap()
        private val homeIds = initial.map { it.id }.toMutableSet()
        private val earthquakes = MutableStateFlow(visibleRows())

        private fun visibleRows(): List<EarthquakeEntity> = rows.values
            .filter { it.id in homeIds }
            .sortedByDescending { it.timestampMillis }

        override fun observeEarthquakes(): Flow<List<EarthquakeEntity>> = earthquakes

        override suspend fun getEarthquake(id: String): EarthquakeEntity? =
            rows[id]

        override suspend fun upsertEarthquakes(earthquakes: List<EarthquakeEntity>) {
            earthquakes.forEach { rows[it.id] = it }
            this.earthquakes.value = visibleRows()
        }

        override suspend fun clearHomeFeed() {
            homeIds.clear()
            earthquakes.value = visibleRows()
        }

        override suspend fun insertHomeFeed(ids: List<HomeFeedEntity>) {
            homeIds += ids.map { it.earthquakeId }
            earthquakes.value = visibleRows()
        }
    }
}
