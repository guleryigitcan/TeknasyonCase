package com.guleryigitcan.teknasyoncase.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsEvent
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsTracker
import com.guleryigitcan.teknasyoncase.analytics.trackSafely
import com.guleryigitcan.teknasyoncase.domain.model.normalizeNotificationThreshold
import com.guleryigitcan.teknasyoncase.domain.repository.NotificationPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlertSettingsViewModel @Inject constructor(
    private val repository: NotificationPreferencesRepository,
    private val analytics: AnalyticsTracker,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertSettingsUiState())
    val uiState: StateFlow<AlertSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.notificationThreshold.collect { threshold ->
                _uiState.update {
                    it.copy(threshold = normalizeNotificationThreshold(threshold), isLoading = false)
                }
            }
        }
    }

    fun saveThreshold(value: Double) {
        viewModelScope.launch {
            try {
                val threshold = normalizeNotificationThreshold(value)
                if (threshold == _uiState.value.threshold) return@launch
                repository.setNotificationThreshold(threshold)
                analytics.trackSafely(AnalyticsEvent.NotificationThresholdChanged(threshold))
            } catch (failure: IOException) {
                _uiState.update { it.copy(hasSaveError = true) }
            }
        }
    }

    fun onPermissionRequested() {
        analytics.trackSafely(AnalyticsEvent.NotificationPermissionRequested)
    }

    fun onPermissionResult(granted: Boolean) {
        analytics.trackSafely(AnalyticsEvent.NotificationPermissionResult(granted))
    }

    fun consumeSaveError() {
        _uiState.update { it.copy(hasSaveError = false) }
    }
}
