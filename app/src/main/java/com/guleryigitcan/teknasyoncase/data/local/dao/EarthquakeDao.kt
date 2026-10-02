package com.guleryigitcan.teknasyoncase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.guleryigitcan.teknasyoncase.data.local.entity.EarthquakeEntity
import com.guleryigitcan.teknasyoncase.data.local.entity.HomeFeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface EarthquakeDao {
    @Query("SELECT earthquakes.* FROM earthquakes INNER JOIN home_feed ON earthquakes.id = home_feed.earthquakeId ORDER BY timestampMillis DESC")
    fun observeEarthquakes(): Flow<List<EarthquakeEntity>>

    @Query("SELECT * FROM earthquakes WHERE id = :id LIMIT 1")
    suspend fun getEarthquake(id: String): EarthquakeEntity?

    @Upsert
    suspend fun upsertEarthquakes(earthquakes: List<EarthquakeEntity>)

    @Query("DELETE FROM home_feed")
    suspend fun clearHomeFeed()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHomeFeed(ids: List<HomeFeedEntity>)

    @Transaction
    suspend fun replaceHomeFeed(earthquakes: List<EarthquakeEntity>) {
        upsertEarthquakes(earthquakes)
        clearHomeFeed()
        insertHomeFeed(earthquakes.map { HomeFeedEntity(it.id) })
    }
}
