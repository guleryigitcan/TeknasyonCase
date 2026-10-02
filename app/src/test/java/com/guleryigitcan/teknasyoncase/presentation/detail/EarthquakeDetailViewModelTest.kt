package com.guleryigitcan.teknasyoncase.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import com.guleryigitcan.teknasyoncase.presentation.navigation.EARTHQUAKE_ID_ARGUMENT
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EarthquakeDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun validIdLoadsContentFromRepository() = runTest {
        val repository = FakeRepository()
        val viewModel = EarthquakeDetailViewModel(SavedStateHandle(mapOf(EARTHQUAKE_ID_ARGUMENT to "us123")), repository)
        assertSame(EarthquakeDetailUiState.Loading, viewModel.uiState.value)
        runCurrent()
        assertEquals("us123", repository.requestedId)

        repository.lookup.complete(earthquake())
        runCurrent()

        val content = viewModel.uiState.value as EarthquakeDetailUiState.Content
        assertEquals("Near Izmir", content.earthquake.place)
        assertEquals(0, repository.refreshCalls)
    }

    @Test
    fun unknownIdShowsNotFound() = runTest {
        val repository = FakeRepository()
        val viewModel = EarthquakeDetailViewModel(SavedStateHandle(mapOf(EARTHQUAKE_ID_ARGUMENT to "missing")), repository)
        runCurrent()

        repository.lookup.complete(null)
        runCurrent()

        assertEquals("missing", repository.requestedId)
        assertSame(EarthquakeDetailUiState.NotFound, viewModel.uiState.value)
        assertEquals(0, repository.refreshCalls)
    }

    @Test
    fun missingSavedIdShowsNotFoundWithoutARepositoryLookup() = runTest {
        val repository = FakeRepository()
        val viewModel = EarthquakeDetailViewModel(SavedStateHandle(), repository)
        runCurrent()

        assertSame(EarthquakeDetailUiState.NotFound, viewModel.uiState.value)
        assertNull(repository.requestedId)
    }

    @Test
    fun localReadFailureShowsErrorState() = runTest {
        val repository = FakeRepository()
        val viewModel = EarthquakeDetailViewModel(SavedStateHandle(mapOf(EARTHQUAKE_ID_ARGUMENT to "us123")), repository)
        runCurrent()

        repository.lookup.completeExceptionally(IllegalStateException("database unavailable"))
        runCurrent()

        assertSame(EarthquakeDetailUiState.Error, viewModel.uiState.value)
    }

    private fun earthquake() = Earthquake(
        id = "us123",
        magnitude = 4.7,
        place = "Near Izmir",
        timestampMillis = 1_767_225_600_000L,
        longitude = 27.1,
        latitude = 38.4,
        depthKm = 8.2,
        usgsUrl = null,
    )

    private class FakeRepository : EarthquakeRepository {
        val lookup = CompletableDeferred<Earthquake?>()
        var requestedId: String? = null
            private set
        var refreshCalls = 0
            private set

        override fun observeEarthquakes(): Flow<List<Earthquake>> = flowOf(emptyList())

        override suspend fun refreshEarthquakes(): Result<Unit> {
            refreshCalls++
            return Result.success(Unit)
        }

        override suspend fun refreshAndGetRecentEarthquakes(
            minimumMagnitude: Double,
            maxResults: Int,
        ): Result<List<Earthquake>> =
            Result.success(emptyList())

        override suspend fun getEarthquake(id: String): Earthquake? {
            requestedId = id
            return lookup.await()
        }
    }
}
