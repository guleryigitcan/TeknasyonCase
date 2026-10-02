package com.guleryigitcan.teknasyoncase.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.guleryigitcan.teknasyoncase.domain.usecase.CheckEarthquakeAlerts
import com.guleryigitcan.teknasyoncase.notification.EarthquakeNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

@HiltWorker
class EarthquakeAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val checkAlerts: CheckEarthquakeAlerts,
    private val notificationManager: EarthquakeNotificationManager,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        val outcome = checkAlerts()
        val candidates = outcome.getOrElse { failure -> return failure.toWorkResult() }
        candidates.forEach(notificationManager::show)
        Result.success()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: Exception) {
        failure.toWorkResult()
    }

    private fun Throwable.toWorkResult(): Result = when {
        this is IOException -> Result.retry()
        this is HttpException && code() >= 500 -> Result.retry()
        else -> Result.failure()
    }
}
