package com.guleryigitcan.teknasyoncase.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationAnalyticsTest {
    @Test
    fun deliveryRecordsSentEventWithOnlyPublicEarthquakeData() {
        val tracker = FakeAnalyticsTracker()

        tracker.recordNotificationSent("us123", 4.7)

        assertEquals(
            listOf(AnalyticsEvent.EarthquakeNotificationSent("us123", 4.7)),
            tracker.events,
        )
    }

    @Test
    fun normalNavigationDoesNotRecordNotificationOpen() {
        val tracker = FakeAnalyticsTracker()
        val openTracker = NotificationOpenTracker(tracker)

        assertNull(openTracker.recordAndGetId(null))
        assertNull(openTracker.recordAndGetId(""))
        assertEquals(emptyList<AnalyticsEvent>(), tracker.events)
    }

    @Test
    fun notificationIdRecordsOpenAndStillNavigatesWhenAnalyticsFails() {
        val tracker = FakeAnalyticsTracker()
        val openTracker = NotificationOpenTracker(tracker)

        assertEquals("us123", openTracker.recordAndGetId("us123"))
        assertEquals(listOf(AnalyticsEvent.EarthquakeNotificationOpened("us123")), tracker.events)

        val failingTracker = object : AnalyticsTracker {
            override fun track(event: AnalyticsEvent) { error("logging failed") }
        }
        assertEquals("us456", NotificationOpenTracker(failingTracker).recordAndGetId("us456"))
    }
}
