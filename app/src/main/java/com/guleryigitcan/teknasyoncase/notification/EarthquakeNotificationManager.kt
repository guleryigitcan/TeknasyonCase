package com.guleryigitcan.teknasyoncase.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.guleryigitcan.teknasyoncase.analytics.AnalyticsTracker
import com.guleryigitcan.teknasyoncase.analytics.recordNotificationSent
import com.guleryigitcan.teknasyoncase.MainActivity
import com.guleryigitcan.teknasyoncase.R
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val EXTRA_EARTHQUAKE_ID = "com.guleryigitcan.teknasyoncase.EARTHQUAKE_ID"

private const val CHANNEL_ID = "earthquake_alerts"

class EarthquakeNotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val analytics: AnalyticsTracker,
) {
    fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT,).apply { description = context.getString(R.string.notification_channel_description) }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun show(earthquake: Earthquake) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = "earthquakes://detail/${android.net.Uri.encode(earthquake.id)}".toUri()
            putExtra(EXTRA_EARTHQUAKE_ID, earthquake.id)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_earthquake_notification)
            .setContentTitle(context.getString(R.string.notification_title, earthquake.magnitude))
            .setContentText(earthquake.place)
            .setStyle(NotificationCompat.BigTextStyle().bigText(earthquake.place))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            manager.notify(earthquake.id, 0, notification)
            analytics.recordNotificationSent(earthquake.id, earthquake.magnitude)
        } catch (_: SecurityException) {
            // Permission may be revoked after the check above.
        }
    }
}
