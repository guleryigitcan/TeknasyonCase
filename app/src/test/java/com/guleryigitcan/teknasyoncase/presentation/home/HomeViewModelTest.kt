package com.guleryigitcan.teknasyoncase.presentation.home

import com.guleryigitcan.teknasyoncase.analytics.AnalyticsEvent
import com.guleryigitcan.teknasyoncase.analytics.FakeAnalyticsTracker
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val tracker = FakeAnalyticsTracker()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun cachedEarthquakesAppearBeforeInitialRefreshFinishes() = runTest {
        val repository = FakeRepository(listOf(earthquake()))
        repository.enqueueRefresh()
        val viewModel = HomeViewModel(repository, tracker)

        runCurrent()

        assertEquals(listOf("us123"), viewModel.uiState.value.earthquakes.map { it.id })
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun initialRefreshSuccessShowsRoomUpdates() = runTest {
        val repository = FakeRepository()
        val refresh = repository.enqueueRefresh()
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        repository.emit(listOf(earthquake()))
        refresh.complete(Result.success(Unit))
        runCurrent()

        assertEquals("us123", viewModel.uiState.value.earthquakes.single().id)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.hasBlockingError)
    }

    @Test
    fun initialFailureWithoutCacheShowsBlockingError() = runTest {
        val repository = FakeRepository()
        val refresh = repository.enqueueRefresh()
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()

        refresh.complete(Result.failure(IllegalStateException("offline")))
        runCurrent()

        assertTrue(viewModel.uiState.value.hasBlockingError)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.hasRefreshError)
    }

    @Test
    fun retryAfterBlockingFailureRestoresLoadingThenEmptyState() = runTest {
        val repository = FakeRepository()
        repository.enqueueRefresh().complete(Result.failure(IllegalStateException("offline")))
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()
        assertTrue(viewModel.uiState.value.hasBlockingError)

        val retry = repository.enqueueRefresh()
        viewModel.refresh()
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.hasBlockingError)

        retry.complete(Result.success(Unit))
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.hasBlockingError)
        assertTrue(viewModel.uiState.value.earthquakes.isEmpty())
    }

    @Test
    fun refreshFailureKeepsCachedContentAndSignalsSnackbar() = runTest {
        val repository = FakeRepository(listOf(earthquake()))
        val refresh = repository.enqueueRefresh()
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()

        refresh.complete(Result.failure(IllegalStateException("offline")))
        runCurrent()

        assertEquals("us123", viewModel.uiState.value.earthquakes.single().id)
        assertTrue(viewModel.uiState.value.hasRefreshError)
        assertFalse(viewModel.uiState.value.hasBlockingError)
        viewModel.consumeRefreshError()
        assertFalse(viewModel.uiState.value.hasRefreshError)
    }

    @Test
    fun manualRefreshTogglesIndicatorAndClearsPreviousError() = runTest {
        val repository = FakeRepository(listOf(earthquake()))
        repository.enqueueRefresh().complete(Result.success(Unit))
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()

        val secondRefresh = repository.enqueueRefresh()
        viewModel.refresh()
        runCurrent()
        assertTrue(viewModel.uiState.value.isRefreshing)
        assertEquals("us123", viewModel.uiState.value.earthquakes.single().id)

        secondRefresh.complete(Result.success(Unit))
        runCurrent()
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.hasRefreshError)
    }

    @Test
    fun overlappingRefreshCallsAreIgnored() = runTest {
        val repository = FakeRepository()
        val firstRefresh = repository.enqueueRefresh()
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()

        viewModel.refresh()
        runCurrent()
        assertEquals(1, repository.refreshCalls)

        firstRefresh.complete(Result.success(Unit))
        runCurrent()
        repository.enqueueRefresh().complete(Result.success(Unit))
        viewModel.refresh()
        runCurrent()
        assertEquals(2, repository.refreshCalls)
    }

    @Test
    fun automaticInitialRefreshDoesNotCountAsManualRefresh() = runTest {
        val repository = FakeRepository()
        repository.enqueueRefresh().complete(Result.success(Unit))
        HomeViewModel(repository, tracker)

        runCurrent()

        assertTrue(tracker.events.isEmpty())
    }

    @Test
    fun pullRefreshAndEarthquakeSelectionEmitInteractionEvents() = runTest {
        val repository = FakeRepository(listOf(earthquake()))
        repository.enqueueRefresh().complete(Result.success(Unit))
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()

        repository.enqueueRefresh().complete(Result.success(Unit))
        viewModel.refreshFromPull()
        viewModel.onEarthquakeSelected("us123")
        runCurrent()

        assertEquals(
            listOf(
                AnalyticsEvent.EarthquakeListRefreshed,
                AnalyticsEvent.EarthquakeSelected("us123"),
            ),
            tracker.events,
        )
    }

    @Test
    fun homeVisibleEmitsListViewed() = runTest {
        val repository = FakeRepository()
        repository.enqueueRefresh().complete(Result.success(Unit))
        val viewModel = HomeViewModel(repository, tracker)
        runCurrent()

        viewModel.onHomeVisible()

        assertEquals(listOf(AnalyticsEvent.EarthquakeListViewed), tracker.events)
    }

    private fun earthquake() = Earthquake(
        id = "us123",
        magnitude = 4.2,
        place = "Near Izmir",
        timestampMillis = 1_700_000_000_000L,
        longitude = 27.1,
        latitude = 38.4,
        depthKm = 12.5,
        usgsUrl = null,
    )

    private class FakeRepository(initial: List<Earthquake> = emptyList()) : EarthquakeRepository {
        private val earthquakes = MutableStateFlow(initial)
        private val refreshes = ArrayDeque<CompletableDeferred<Result<Unit>>>()
        var refreshCalls = 0
            private set

        fun emit(items: List<Earthquake>) {
            earthquakes.value = items
        }

        fun enqueueRefresh(): CompletableDeferred<Result<Unit>> =
            CompletableDeferred<Result<Unit>>().also(refreshes::addLast)

        override fun observeEarthquakes(): Flow<List<Earthquake>> = earthquakes

        override suspend fun refreshEarthquakes(): Result<Unit> {
            refreshCalls++
            return refreshes.removeFirst().await()
        }

        override suspend fun refreshAndGetRecentEarthquakes(
            minimumMagnitude: Double,
            maxResults: Int,
        ): Result<List<Earthquake>> =
            Result.success(earthquakes.value)

        override suspend fun getEarthquake(id: String): Earthquake? =
            earthquakes.value.firstOrNull { it.id == id }
    }
}
