package com.guleryigitcan.teknasyoncase.analytics

import android.util.Log
import javax.inject.Inject

class LocalAnalyticsTracker @Inject constructor() : AnalyticsTracker {
    override fun track(event: AnalyticsEvent) {
        val message = when (event) {
            AnalyticsEvent.AppOpened -> "app_opened"
            AnalyticsEvent.EarthquakeListViewed -> "earthquake_list_viewed"
            is AnalyticsEvent.EarthquakeSelected ->
                "earthquake_selected earthquake_id=${event.earthquakeId}"
            AnalyticsEvent.EarthquakeListRefreshed -> "earthquake_list_refreshed"
            is AnalyticsEvent.NotificationThresholdChanged ->
                "notification_threshold_changed threshold=${event.threshold}"
            AnalyticsEvent.NotificationPermissionRequested -> "notification_permission_requested"
            is AnalyticsEvent.NotificationPermissionResult ->
                "notification_permission_result granted=${event.granted}"
            is AnalyticsEvent.EarthquakeNotificationSent ->
                "earthquake_notification_sent earthquake_id=${event.earthquakeId} magnitude=${event.magnitude}"
            is AnalyticsEvent.EarthquakeNotificationOpened ->
                "earthquake_notification_opened earthquake_id=${event.earthquakeId}"
        }
        Log.d("EarthquakeAnalytics", message)
    }
}
