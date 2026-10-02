package com.guleryigitcan.teknasyoncase.presentation.detail

import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import com.guleryigitcan.teknasyoncase.presentation.detail.model.toDetailUiModel
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EarthquakeDetailUiModelTest {
    @Test
    fun formatsExactLocalTimeDepthAndCoordinates() {
        val uiModel = earthquake().toDetailUiModel(Locale.US, TimeZone.getTimeZone("Europe/Istanbul"))

        assertEquals("4.7", uiModel.magnitudeText)
        assertEquals("Near Izmir", uiModel.place)
        assertEquals("1 Jan 2026, 03:00", uiModel.occurredText)
        assertEquals("8.2 km", uiModel.depthText)
        assertEquals("38.423°", uiModel.latitudeText)
        assertEquals("-27.142°", uiModel.longitudeText)
        assertEquals(EarthquakeSeverity.STRONG, uiModel.severity)
    }

    @Test
    fun missingDepthRemainsUnknownAndSeverityIsShared() {
        val uiModel = earthquake(magnitude = 5.0, depthKm = null)
            .toDetailUiModel(Locale.US, TimeZone.getTimeZone("UTC"))

        assertNull(uiModel.depthText)
        assertEquals(EarthquakeSeverity.SEVERE, uiModel.severity)
    }

    private fun earthquake(magnitude: Double = 4.7, depthKm: Double? = 8.2) = Earthquake(
        id = "us123",
        magnitude = magnitude,
        place = "Near Izmir",
        timestampMillis = 1_767_225_600_000L,
        longitude = -27.1424,
        latitude = 38.4231,
        depthKm = depthKm,
        usgsUrl = null,
    )
}
