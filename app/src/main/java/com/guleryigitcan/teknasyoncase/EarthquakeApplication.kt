package com.guleryigitcan.teknasyoncase

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsEvent
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsTracker
import com.guleryigitcan.teknasyoncase.analytics.trackSafely
import com.guleryigitcan.teknasyoncase.notification.EarthquakeNotificationManager
import com.guleryigitcan.teknasyoncase.worker.EarthquakeAlertScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EarthquakeApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var alertScheduler: EarthquakeAlertScheduler
    @Inject lateinit var notificationManager: EarthquakeNotificationManager
    @Inject lateinit var analytics: AnalyticsTracker

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        analytics.trackSafely(AnalyticsEvent.AppOpened)
        notificationManager.createChannel()
        alertScheduler.schedule()
    }
}
