package com.guleryigitcan.teknasyoncase.presentation.home

import com.guleryigitcan.teknasyoncase.presentation.home.model.EarthquakeUiModel

data class HomeUiState(
    val earthquakes: List<EarthquakeUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasBlockingError: Boolean = false,
    val hasRefreshError: Boolean = false,
)
