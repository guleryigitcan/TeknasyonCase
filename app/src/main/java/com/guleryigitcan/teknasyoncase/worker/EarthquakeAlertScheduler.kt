package com.guleryigitcan.teknasyoncase.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EarthquakeAlertScheduler @Inject constructor(@param:ApplicationContext private val context: Context) {
    fun schedule() {
        val request = PeriodicWorkRequestBuilder<EarthquakeAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "earthquake_alert_check",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
