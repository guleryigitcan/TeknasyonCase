package com.guleryigitcan.teknasyoncase.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.guleryigitcan.teknasyoncase.domain.model.DEFAULT_NOTIFICATION_THRESHOLD
import com.guleryigitcan.teknasyoncase.domain.model.normalizeNotificationThreshold
import com.guleryigitcan.teknasyoncase.domain.repository.NotificationPreferencesRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class NotificationPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : NotificationPreferencesRepository {
    override val notificationThreshold: Flow<Double> = dataStore.data
        .catch { failure ->
            if (failure is IOException) emit(emptyPreferences()) else throw failure
        }
        .map { it.threshold() }

    override suspend fun readThresholdForMonitoring(): Double = dataStore.data.first().threshold()

    override suspend fun setNotificationThreshold(value: Double) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_THRESHOLD_KEY] = normalizeNotificationThreshold(value)
        }
    }

    private fun Preferences.threshold(): Double = normalizeNotificationThreshold(
        this[NOTIFICATION_THRESHOLD_KEY] ?: DEFAULT_NOTIFICATION_THRESHOLD,
    )

    private companion object {
        val NOTIFICATION_THRESHOLD_KEY = doublePreferencesKey("notification_threshold")
    }
}
