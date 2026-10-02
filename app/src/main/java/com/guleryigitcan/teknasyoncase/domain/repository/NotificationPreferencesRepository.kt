package com.guleryigitcan.teknasyoncase.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    val notificationThreshold: Flow<Double>

    suspend fun readThresholdForMonitoring(): Double

    suspend fun setNotificationThreshold(value: Double)
}
