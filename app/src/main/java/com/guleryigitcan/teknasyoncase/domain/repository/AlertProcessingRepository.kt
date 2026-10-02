package com.guleryigitcan.teknasyoncase.domain.repository

interface AlertProcessingRepository {
    suspend fun claimNewIds(ids: List<String>): Set<String>
}
