package com.guleryigitcan.teknasyoncase.analytics

import javax.inject.Inject

class NotificationOpenTracker @Inject constructor(private val analytics: AnalyticsTracker) {
    fun recordAndGetId(notificationEarthquakeId: String?): String? {
        val id = notificationEarthquakeId?.takeIf(String::isNotBlank) ?: return null
        analytics.trackSafely(AnalyticsEvent.EarthquakeNotificationOpened(id))
        return id
    }
}
