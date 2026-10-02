package com.guleryigitcan.teknasyoncase.domain.usecase

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.domain.model.MIN_NOTIFICATION_THRESHOLD
import com.guleryigitcan.teknasyoncase.domain.repository.AlertProcessingRepository
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import com.guleryigitcan.teknasyoncase.domain.repository.NotificationPreferencesRepository
import javax.inject.Inject

class CheckEarthquakeAlerts @Inject constructor(
    private val earthquakes: EarthquakeRepository,
    private val preferences: NotificationPreferencesRepository,
    private val processing: AlertProcessingRepository,
) {
    suspend operator fun invoke(): Result<List<Earthquake>> {
        val threshold = preferences.readThresholdForMonitoring()
        val recent = earthquakes.refreshAndGetRecentEarthquakes(
            minimumMagnitude = MIN_NOTIFICATION_THRESHOLD,
            maxResults = 500,
        ).getOrElse {
            return Result.failure(it)
        }
        val newIds = processing.claimNewIds(recent.map(Earthquake::id))
        return Result.success(
            recent.filter { it.id in newIds && it.magnitude >= threshold }
                .sortedWith(compareBy<Earthquake> { it.timestampMillis }.thenBy { it.id }),
        )
    }
}
