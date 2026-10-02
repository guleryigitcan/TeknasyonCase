package com.guleryigitcan.teknasyoncase.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class NotificationPreferencesDataStoreTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun missingPreferenceEmitsDefaultThreshold() = runTest {
        val repository = NotificationPreferencesDataStore(createDataStore())

        assertEquals(4.0, repository.notificationThreshold.first(), 0.0)
    }

    @Test
    fun storedThresholdIsEmitted() = runTest {
        val dataStore = createDataStore()
        dataStore.edit { it[doublePreferencesKey("notification_threshold")] = 5.5 }
        val repository = NotificationPreferencesDataStore(dataStore)

        assertEquals(5.5, repository.notificationThreshold.first(), 0.0)
    }

    @Test
    fun settingThresholdPersistsValue() = runTest {
        val dataStore = createDataStore()
        val repository = NotificationPreferencesDataStore(dataStore)

        repository.setNotificationThreshold(3.5)

        assertEquals(3.5, dataStore.data.first()[doublePreferencesKey("notification_threshold")]!!, 0.0)
        assertEquals(3.5, repository.notificationThreshold.first(), 0.0)
    }

    @Test
    fun outOfRangeStoredValueIsNormalized() = runTest {
        val dataStore = createDataStore()
        dataStore.edit { it[doublePreferencesKey("notification_threshold")] = 8.0 }
        val repository = NotificationPreferencesDataStore(dataStore)

        assertEquals(7.0, repository.notificationThreshold.first(), 0.0)
    }

    @Test
    fun monitoringReadPropagatesStorageErrorInsteadOfUsingDefault() = runTest {
        val failingStore = object : DataStore<Preferences> {
            override val data: Flow<Preferences> = flow { throw IOException("read failed") }
            override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
                throw IOException("write failed")
        }
        val repository = NotificationPreferencesDataStore(failingStore)

        assertEquals(4.0, repository.notificationThreshold.first(), 0.0)
        try {
            repository.readThresholdForMonitoring()
            fail("Monitoring must not continue with an assumed threshold")
        } catch (_: IOException) {
            // WorkManager can retry without claiming any earthquake IDs.
        }
    }

    private fun TestScope.createDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = backgroundScope,
        produceFile = { File(temporaryFolder.root, "alerts.preferences_pb") },
    )
}
