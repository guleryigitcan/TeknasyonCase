package com.guleryigitcan.teknasyoncase.domain.usecase

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.domain.repository.AlertProcessingRepository
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import com.guleryigitcan.teknasyoncase.domain.repository.NotificationPreferencesRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckEarthquakeAlertsTest {
    @Test
    fun firstSuccessfulRunSilentlyBaselinesExistingQuakes() = runBlocking {
        val fixture = Fixture(listOf(earthquake("existing", 5.0)))

        assertTrue(fixture.check().getOrThrow().isEmpty())
        assertEquals(setOf("existing"), fixture.processing.processed)
        assertTrue(fixture.processing.baselined)
        assertEquals(2.0 to 500, fixture.earthquakes.lastRequest)
    }

    @Test
    fun thresholdIsInclusiveAndAllNewIdsAreProcessed() = runBlocking {
        val fixture = Fixture(emptyList())
        fixture.check().getOrThrow()
        fixture.earthquakes.feed = Result.success(
            listOf(
                earthquake("below", 3.9),
                earthquake("equal", 4.0, 1),
                earthquake("above", 4.5, 2),
            ),
        )

        assertEquals(listOf("equal", "above"), fixture.check().getOrThrow().map { it.id })
        assertEquals(setOf("below", "equal", "above"), fixture.processing.processed)
    }

    @Test
    fun subsequentRunsDoNotRepeatAlertsOrAlertOldQuakesAfterThresholdLowers() = runBlocking {
        val fixture = Fixture(emptyList(), threshold = 4.5)
        fixture.check().getOrThrow()
        fixture.earthquakes.feed = Result.success(listOf(earthquake("old", 4.2), earthquake("new", 5.0)))

        assertEquals(listOf("new"), fixture.check().getOrThrow().map { it.id })
        fixture.preferences.value = 4.0
        assertTrue(fixture.check().getOrThrow().isEmpty())
        assertEquals(setOf("old", "new"), fixture.processing.processed)
    }

    @Test
    fun failedFetchDoesNotEstablishBaselineOrAdvanceProcessedIds() = runBlocking {
        val fixture = Fixture(listOf(earthquake("old", 5.0)))
        fixture.earthquakes.feed = Result.failure(IOException("offline"))

        assertTrue(fixture.check().isFailure)
        assertFalse(fixture.processing.baselined)
        assertTrue(fixture.processing.processed.isEmpty())
        fixture.earthquakes.feed = Result.success(listOf(earthquake("old", 5.0)))
        assertTrue(fixture.check().getOrThrow().isEmpty())
    }

    @Test
    fun unreadableThresholdDoesNotFetchOrAdvanceProcessedIds() = runBlocking {
        val fixture = Fixture(listOf(earthquake("existing", 5.0)))
        fixture.preferences.failReads = true

        assertTrue(runCatching { fixture.check() }.isFailure)
        assertEquals(null, fixture.earthquakes.lastRequest)
        assertFalse(fixture.processing.baselined)
        assertTrue(fixture.processing.processed.isEmpty())
    }

    @Test
    fun multipleNewCandidatesHaveDeterministicOrder() = runBlocking {
        val fixture = Fixture(emptyList())
        fixture.check().getOrThrow()
        fixture.earthquakes.feed = Result.success(
            listOf(
                earthquake("z", 5.0, 200),
                earthquake("b", 5.0, 100),
                earthquake("a", 5.0, 100),
            ),
        )

        assertEquals(listOf("a", "b", "z"), fixture.check().getOrThrow().map { it.id })
    }

    private class Fixture(initial: List<Earthquake>, threshold: Double = 4.0) {
        val earthquakes = FakeEarthquakes(Result.success(initial))
        val preferences = FakePreferences(threshold)
        val processing = FakeProcessing()
        val check = CheckEarthquakeAlerts(earthquakes, preferences, processing)
    }

    private class FakeEarthquakes(var feed: Result<List<Earthquake>>) : EarthquakeRepository {
        var lastRequest: Pair<Double, Int>? = null
        override fun observeEarthquakes(): Flow<List<Earthquake>> = flowOf(emptyList())
        override suspend fun refreshEarthquakes(): Result<Unit> = feed.map { Unit }
        override suspend fun refreshAndGetRecentEarthquakes(
            minimumMagnitude: Double,
            maxResults: Int,
        ): Result<List<Earthquake>> {
            lastRequest = minimumMagnitude to maxResults
            return feed
        }
        override suspend fun getEarthquake(id: String): Earthquake? = null
    }

    private class FakePreferences(initial: Double) : NotificationPreferencesRepository {
        private val threshold = MutableStateFlow(initial)
        var failReads = false
        var value: Double
            get() = threshold.value
            set(value) { threshold.value = value }
        override val notificationThreshold: Flow<Double> = threshold
        override suspend fun readThresholdForMonitoring(): Double {
            if (failReads) throw IOException("preferences unavailable")
            return threshold.value
        }
        override suspend fun setNotificationThreshold(value: Double) { threshold.value = value }
    }

    private class FakeProcessing : AlertProcessingRepository {
        var baselined = false
        val processed = mutableSetOf<String>()
        override suspend fun claimNewIds(ids: List<String>): Set<String> {
            val newIds = if (baselined) ids.toSet() - processed else emptySet()
            processed += ids
            baselined = true
            return newIds
        }
    }

    private fun earthquake(id: String, magnitude: Double, time: Long = 0) = Earthquake(
        id = id,
        magnitude = magnitude,
        place = "Near Izmir",
        timestampMillis = time,
        longitude = 27.1,
        latitude = 38.4,
        depthKm = null,
        usgsUrl = null,
    )
}
