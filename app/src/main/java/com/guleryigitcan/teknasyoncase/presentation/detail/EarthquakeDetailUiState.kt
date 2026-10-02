package com.guleryigitcan.teknasyoncase.presentation.detail

import com.guleryigitcan.teknasyoncase.presentation.detail.model.EarthquakeDetailUiModel

sealed interface EarthquakeDetailUiState {
    data object Loading : EarthquakeDetailUiState
    data class Content(val earthquake: EarthquakeDetailUiModel) : EarthquakeDetailUiState
    data object NotFound : EarthquakeDetailUiState
    data object Error : EarthquakeDetailUiState
}
