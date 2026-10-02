package com.guleryigitcan.teknasyoncase.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsEvent
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsTracker
import com.guleryigitcan.teknasyoncase.analytics.trackSafely
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import com.guleryigitcan.teknasyoncase.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: EarthquakeRepository,
    private val analytics: AnalyticsTracker,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var refreshInProgress = false

    init {
        viewModelScope.launch {
            repository.observeEarthquakes().collect { earthquakes ->
                val uiModels = earthquakes.map { it.toUiModel() }
                _uiState.update { state ->
                    state.copy(
                        earthquakes = uiModels,
                        isLoading = state.isLoading && uiModels.isEmpty(),
                        isRefreshing = state.isRefreshing || (refreshInProgress && uiModels.isNotEmpty()),
                        hasBlockingError = state.hasBlockingError && uiModels.isEmpty(),
                        hasRefreshError = state.hasRefreshError ||
                            (state.hasBlockingError && uiModels.isNotEmpty()),
                    )
                }
            }
        }
        refresh()
    }

    fun refresh() {
        if (refreshInProgress) return
        refreshInProgress = true
        _uiState.update { state ->
            state.copy(
                isLoading = state.isLoading || state.hasBlockingError,
                isRefreshing = !state.isLoading && !state.hasBlockingError,
                hasBlockingError = false,
                hasRefreshError = false,
            )
        }
        viewModelScope.launch {
            try {
                val result = repository.refreshEarthquakes()
                _uiState.update { state ->
                    val failed = result.isFailure
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasBlockingError = failed && state.earthquakes.isEmpty(),
                        hasRefreshError = failed && state.earthquakes.isNotEmpty(),
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } finally {
                refreshInProgress = false
            }
        }
    }

    fun refreshFromPull() {
        if (refreshInProgress) return
        analytics.trackSafely(AnalyticsEvent.EarthquakeListRefreshed)
        refresh()
    }

    fun onHomeVisible() {
        analytics.trackSafely(AnalyticsEvent.EarthquakeListViewed)
    }

    fun onEarthquakeSelected(id: String) {
        analytics.trackSafely(AnalyticsEvent.EarthquakeSelected(id))
    }

    fun consumeRefreshError() {
        _uiState.update { it.copy(hasRefreshError = false) }
    }
}
