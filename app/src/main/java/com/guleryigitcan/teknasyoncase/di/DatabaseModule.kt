package com.guleryigitcan.teknasyoncase.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.guleryigitcan.teknasyoncase.data.local.EarthquakeDatabase
import com.guleryigitcan.teknasyoncase.data.local.dao.AlertProcessingDao
import com.guleryigitcan.teknasyoncase.data.local.dao.EarthquakeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideEarthquakeDatabase(
        @ApplicationContext context: Context
    ): EarthquakeDatabase =
        Room.databaseBuilder(
            context,
            EarthquakeDatabase::class.java,
            "earthquakes.db"
        ).build()

    @Provides
    fun provideEarthquakeDao(
        database: EarthquakeDatabase
    ): EarthquakeDao =
        database.earthquakeDao()

    @Provides
    fun provideAlertProcessingDao(
        database: EarthquakeDatabase
    ): AlertProcessingDao =
        database.alertProcessingDao()
}
