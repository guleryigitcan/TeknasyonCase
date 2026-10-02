package com.guleryigitcan.teknasyoncase.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import com.guleryigitcan.teknasyoncase.presentation.detail.model.toDetailUiModel
import com.guleryigitcan.teknasyoncase.presentation.navigation.EARTHQUAKE_ID_ARGUMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EarthquakeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: EarthquakeRepository,
) : ViewModel() {
    private val earthquakeId: String? = savedStateHandle[EARTHQUAKE_ID_ARGUMENT]

    private val _uiState = MutableStateFlow<EarthquakeDetailUiState>(EarthquakeDetailUiState.Loading)
    val uiState: StateFlow<EarthquakeDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = try {
                earthquakeId?.let { repository.getEarthquake(it) }
                    ?.let { EarthquakeDetailUiState.Content(it.toDetailUiModel()) }
                    ?: EarthquakeDetailUiState.NotFound
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (failure: Exception) {
                EarthquakeDetailUiState.Error
            }
        }
    }
}
