package com.guleryigitcan.teknasyoncase.data.repository

import androidx.room.withTransaction
import com.guleryigitcan.teknasyoncase.data.local.EarthquakeDatabase
import com.guleryigitcan.teknasyoncase.data.local.dao.AlertProcessingDao
import com.guleryigitcan.teknasyoncase.data.local.entity.AlertMonitorStateEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.ProcessedEarthquakeEntity
import com.guleryigitcan.teknasyoncase.domain.repository.AlertProcessingRepository
import javax.inject.Inject

internal class AlertProcessingRepositoryImpl @Inject constructor(
    private val database: EarthquakeDatabase,
    private val dao: AlertProcessingDao,
) : AlertProcessingRepository {
    override suspend fun claimNewIds(ids: List<String>): Set<String> = database.withTransaction {
        val uniqueIds = ids.distinct()
        if (!dao.hasBaseline()) {
            dao.insertProcessed(uniqueIds.map(::ProcessedEarthquakeEntity))
            dao.insertBaseline(AlertMonitorStateEntity())
            emptySet()
        } else {
            val newIds = uniqueIds.toSet() - dao.existingIds(uniqueIds).toSet()
            dao.insertProcessed(newIds.map(::ProcessedEarthquakeEntity))
            newIds
        }
    }
}
