package com.guleryigitcan.teknasyoncase.presentation.settings

import com.guleryigitcan.teknasyoncase.domain.model.DEFAULT_NOTIFICATION_THRESHOLD

data class AlertSettingsUiState(
    val threshold: Double = DEFAULT_NOTIFICATION_THRESHOLD,
    val isLoading: Boolean = true,
    val hasSaveError: Boolean = false,
)
