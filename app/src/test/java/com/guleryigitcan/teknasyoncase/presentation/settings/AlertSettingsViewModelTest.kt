package com.guleryigitcan.teknasyoncase.presentation.settings

import com.guleryigitcan.teknasyoncase.analytics.AnalyticsEvent
import com.guleryigitcan.teknasyoncase.analytics.FakeAnalyticsTracker
import com.guleryigitcan.teknasyoncase.domain.repository.NotificationPreferencesRepository
import java.io.IOException
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
class AlertSettingsViewModelTest {
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
    fun persistedThresholdIsExposedAfterLoading() = runTest {
        val repository = FakePreferencesRepository(5.5)
        val viewModel = AlertSettingsViewModel(repository, tracker)
        assertTrue(viewModel.uiState.value.isLoading)

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(5.5, viewModel.uiState.value.threshold, 0.0)
    }

    @Test
    fun savingThresholdUpdatesRepositoryAndUi() = runTest {
        val repository = FakePreferencesRepository()
        val viewModel = AlertSettingsViewModel(repository, tracker)
        runCurrent()

        viewModel.saveThreshold(4.5)
        runCurrent()

        assertEquals(4.5, repository.lastSaved!!, 0.0)
        assertEquals(4.5, viewModel.uiState.value.threshold, 0.0)
        assertEquals(listOf(AnalyticsEvent.NotificationThresholdChanged(4.5)), tracker.events)
    }

    @Test
    fun savingNormalizesToSupportedHalfMagnitudeStep() = runTest {
        val repository = FakePreferencesRepository()
        val viewModel = AlertSettingsViewModel(repository, tracker)
        runCurrent()

        viewModel.saveThreshold(7.8)
        runCurrent()
        assertEquals(7.0, repository.lastSaved!!, 0.0)

        viewModel.saveThreshold(2.26)
        runCurrent()
        assertEquals(2.5, repository.lastSaved!!, 0.0)
    }

    @Test
    fun writeFailureIsExposedAndCanBeConsumed() = runTest {
        val repository = FakePreferencesRepository().apply { failWrites = true }
        val viewModel = AlertSettingsViewModel(repository, tracker)
        runCurrent()

        viewModel.saveThreshold(5.0)
        runCurrent()

        assertTrue(viewModel.uiState.value.hasSaveError)
        assertEquals(4.0, viewModel.uiState.value.threshold, 0.0)
        viewModel.consumeSaveError()
        assertFalse(viewModel.uiState.value.hasSaveError)
        assertTrue(tracker.events.isEmpty())
    }

    @Test
    fun savingUnchangedThresholdDoesNotReportChange() = runTest {
        val repository = FakePreferencesRepository()
        val viewModel = AlertSettingsViewModel(repository, tracker)
        runCurrent()

        viewModel.saveThreshold(4.0)
        runCurrent()

        assertTrue(tracker.events.isEmpty())
    }

    @Test
    fun permissionCallbacksTrackActualRequestAndResult() = runTest {
        val repository = FakePreferencesRepository()
        val viewModel = AlertSettingsViewModel(repository, tracker)

        viewModel.onPermissionRequested()
        viewModel.onPermissionResult(false)

        assertEquals(
            listOf(
                AnalyticsEvent.NotificationPermissionRequested,
                AnalyticsEvent.NotificationPermissionResult(false),
            ),
            tracker.events,
        )
    }

    private class FakePreferencesRepository(initial: Double = 4.0) : NotificationPreferencesRepository {
        private val threshold = MutableStateFlow(initial)
        override val notificationThreshold: Flow<Double> = threshold
        override suspend fun readThresholdForMonitoring(): Double = threshold.value
        var lastSaved: Double? = null
            private set
        var failWrites = false

        override suspend fun setNotificationThreshold(value: Double) {
            if (failWrites) throw IOException("disk unavailable")
            lastSaved = value
            threshold.value = value
        }
    }
}
