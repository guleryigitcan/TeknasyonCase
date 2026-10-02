package com.guleryigitcan.teknasyoncase.analytics

interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
}

fun AnalyticsTracker.trackSafely(event: AnalyticsEvent) {
    try {
        track(event)
    } catch (_: Exception) { }
}

fun AnalyticsTracker.recordNotificationSent(earthquakeId: String, magnitude: Double) {
    trackSafely(AnalyticsEvent.EarthquakeNotificationSent(earthquakeId, magnitude))
}
