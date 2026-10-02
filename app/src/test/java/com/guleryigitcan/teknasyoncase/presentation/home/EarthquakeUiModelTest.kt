package com.guleryigitcan.teknasyoncase.presentation.home

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.presentation.home.model.toUiModel
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EarthquakeUiModelTest {
    @Test
    fun mapsMagnitudeDepthAndDeviceLocalTime() {
        val earthquake = earthquake(magnitude = 4.2, depthKm = 8.25)

        val uiModel = earthquake.toUiModel(Locale.US, TimeZone.getTimeZone("Europe/Istanbul"))

        assertEquals("4.2", uiModel.magnitudeText)
        assertEquals("8.3", uiModel.depthText)
        assertEquals("1 Jan, 03:00", uiModel.timeText)
        assertEquals(EarthquakeSeverity.STRONG, uiModel.severity)
    }

    @Test
    fun severityBoundariesAndMissingDepthAreExplicit() {
        assertEquals(EarthquakeSeverity.MINOR, earthquake(2.9).toUiModel().severity)
        assertEquals(EarthquakeSeverity.MODERATE, earthquake(3.0).toUiModel().severity)
        assertEquals(EarthquakeSeverity.STRONG, earthquake(4.0).toUiModel().severity)
        assertEquals(EarthquakeSeverity.SEVERE, earthquake(5.0).toUiModel().severity)
        assertNull(earthquake(3.0).toUiModel().depthText)
    }

    private fun earthquake(magnitude: Double, depthKm: Double? = null) = Earthquake(
        id = "us123",
        magnitude = magnitude,
        place = "Near Izmir",
        timestampMillis = 1_767_225_600_000L,
        longitude = 27.1,
        latitude = 38.4,
        depthKm = depthKm,
        usgsUrl = null,
    )
}
