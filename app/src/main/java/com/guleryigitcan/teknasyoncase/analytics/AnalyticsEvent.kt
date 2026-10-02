package com.guleryigitcan.teknasyoncase.analytics

sealed interface AnalyticsEvent {
    data object AppOpened : AnalyticsEvent
    data object EarthquakeListViewed : AnalyticsEvent
    data class EarthquakeSelected(val earthquakeId: String) : AnalyticsEvent
    data object EarthquakeListRefreshed : AnalyticsEvent
    data class NotificationThresholdChanged(val threshold: Double) : AnalyticsEvent
    data object NotificationPermissionRequested : AnalyticsEvent
    data class NotificationPermissionResult(val granted: Boolean) : AnalyticsEvent
    data class EarthquakeNotificationSent(val earthquakeId: String, val magnitude: Double) : AnalyticsEvent
    data class EarthquakeNotificationOpened(val earthquakeId: String) : AnalyticsEvent
}
