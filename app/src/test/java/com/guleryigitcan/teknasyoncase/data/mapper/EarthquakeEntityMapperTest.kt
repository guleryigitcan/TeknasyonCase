package com.guleryigitcan.teknasyoncase.data.mapper

import com.guleryigitcan.teknasyoncase.data.local.entity.EarthquakeEntity
import com.guleryigitcan.teknasyoncase.domain.model.Earthquake
import org.junit.Assert.assertEquals
import org.junit.Test

class EarthquakeEntityMapperTest {
    @Test
    fun entityMapsAllPersistedFieldsToDomain() {
        val entity = EarthquakeEntity(
            id = "us123",
            magnitude = 4.2,
            place = "Near Izmir",
            timestampMillis = 1_700_000_000_000L,
            longitude = 27.1,
            latitude = 38.4,
            depthKm = null,
            usgsUrl = "https://earthquake.usgs.gov/earthquakes/eventpage/us123",
        )

        assertEquals(
            Earthquake("us123", 4.2, "Near Izmir", 1_700_000_000_000L, 27.1, 38.4, null, entity.usgsUrl),
            entity.toDomain(),
        )
    }

    @Test
    fun domainMapsAllFieldsToEntity() {
        val earthquake = Earthquake(
            id = "us123",
            magnitude = 4.2,
            place = "Near Izmir",
            timestampMillis = 1_700_000_000_000L,
            longitude = 27.1,
            latitude = 38.4,
            depthKm = 12.5,
            usgsUrl = null,
        )

        assertEquals(
            EarthquakeEntity("us123", 4.2, "Near Izmir", 1_700_000_000_000L, 27.1, 38.4, 12.5, null),
            earthquake.toEntity(),
        )
    }
}
