package com.guleryigitcan.teknasyoncase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "processed_earthquakes")
internal data class ProcessedEarthquakeEntity(@PrimaryKey val earthquakeId: String)
