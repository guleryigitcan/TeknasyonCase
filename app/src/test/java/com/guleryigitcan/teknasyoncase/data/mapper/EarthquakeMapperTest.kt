package com.guleryigitcan.teknasyoncase.data.mapper

import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeFeatureDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeGeometryDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakePropertiesDto
import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeResponseDto
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class EarthquakeMapperTest {
    @Test
    fun validFeatureMapsCoordinatesInGeoJsonOrder() {
        val earthquake = feature().toDomain()!!

        assertEquals("us123", earthquake.id)
        assertEquals(4.2, earthquake.magnitude, 0.0)
        assertEquals("Near Izmir", earthquake.place)
        assertEquals(1_700_000_000_000L, earthquake.timestampMillis)
        assertEquals(27.1, earthquake.longitude, 0.0)
        assertEquals(38.4, earthquake.latitude, 0.0)
        assertEquals(12.5, earthquake.depthKm!!, 0.0)
        assertEquals("https://earthquake.usgs.gov/earthquakes/eventpage/us123", earthquake.usgsUrl)
    }

    @Test
    fun missingMagnitudeSkipsFeatureWithoutDroppingOtherEarthquakes() {
        val response = EarthquakeResponseDto(
            listOf(feature(properties = EarthquakePropertiesDto(time = 1L)), feature()),
        )

        assertEquals(listOf("us123"), response.toDomain().map { it.id })
    }

    @Test
    fun missingPlaceUsesFallback() {
        val earthquake = feature(
            properties = EarthquakePropertiesDto(magnitude = 4.2, time = 1L),
        ).toDomain()!!

        assertEquals("Unknown location", earthquake.place)
    }

    @Test
    fun incompleteOrMissingCoordinatesSkipFeature() {
        assertNull(feature(geometry = EarthquakeGeometryDto(listOf(27.1))).toDomain())
        assertNull(feature(geometry = null).toDomain())
        assertNull(feature(geometry = EarthquakeGeometryDto(listOf(27.1, null, 12.5))).toDomain())
    }

    @Test
    fun missingDepthIsRetainedAsUnknown() {
        val earthquake = feature(geometry = EarthquakeGeometryDto(listOf(27.1, 38.4))).toDomain()!!

        assertNull(earthquake.depthKm)
    }

    @Test
    fun missingFeaturesIsMalformedWhileExplicitlyEmptyFeedIsValid() {
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<EarthquakeResponseDto>("{}")
        }
        assertEquals(
            emptyList<String>(),
            Json.decodeFromString<EarthquakeResponseDto>("{\"features\":[]}").toDomain().map { it.id },
        )
    }

    @Test
    fun unusableIdentityTimeAndOutOfRangeLocationAreSkipped() {
        assertNull(
            EarthquakeFeatureDto(
                id = "",
                properties = EarthquakePropertiesDto(magnitude = 4.2, time = 1L),
                geometry = EarthquakeGeometryDto(listOf(27.1, 38.4)),
            ).toDomain(),
        )
        assertNull(feature(properties = EarthquakePropertiesDto(magnitude = 4.2)).toDomain())
        assertNull(feature(geometry = EarthquakeGeometryDto(listOf(181.0, 38.4))).toDomain())
    }

    private fun feature(
        properties: EarthquakePropertiesDto? = EarthquakePropertiesDto(
            magnitude = 4.2,
            place = "Near Izmir",
            time = 1_700_000_000_000L,
            url = "https://earthquake.usgs.gov/earthquakes/eventpage/us123",
        ),
        geometry: EarthquakeGeometryDto? = EarthquakeGeometryDto(listOf(27.1, 38.4, 12.5)),
    ) = EarthquakeFeatureDto(id = "us123", properties = properties, geometry = geometry)
}
