package com.guleryigitcan.teknasyoncase.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guleryigitcan.teknasyoncase.data.local.dao.AlertProcessingDao
import com.guleryigitcan.teknasyoncase.data.local.dao.EarthquakeDao
import com.guleryigitcan.teknasyoncase.data.local.entity.AlertMonitorStateEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.EarthquakeEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.HomeFeedEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.ProcessedEarthquakeEntity

@Database(
    entities = [EarthquakeEntity::class, ProcessedEarthquakeEntity::class, AlertMonitorStateEntity::class, HomeFeedEntity::class],
    version = 3,
    exportSchema = false,
)
internal abstract class EarthquakeDatabase : RoomDatabase() {
    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun alertProcessingDao(): AlertProcessingDao
}
