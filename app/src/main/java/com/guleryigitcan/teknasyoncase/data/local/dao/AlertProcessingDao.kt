package com.guleryigitcan.teknasyoncase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guleryigitcan.teknasyoncase.data.local.entity.AlertMonitorStateEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.ProcessedEarthquakeEntity

@Dao
internal interface AlertProcessingDao {
    @Query("SELECT EXISTS(SELECT 1 FROM alert_monitor_state WHERE id = 0)")
    suspend fun hasBaseline(): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBaseline(state: AlertMonitorStateEntity)

    @Query("SELECT earthquakeId FROM processed_earthquakes WHERE earthquakeId IN (:ids)")
    suspend fun existingIds(ids: List<String>): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProcessed(earthquakes: List<ProcessedEarthquakeEntity>)
}
