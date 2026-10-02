package com.guleryigitcan.teknasyoncase.di

import com.guleryigitcan.teknasyoncase.data.repository.EarthquakeRepositoryImpl
import com.guleryigitcan.teknasyoncase.data.repository.AlertProcessingRepositoryImpl
import com.guleryigitcan.teknasyoncase.data.local.preferences.NotificationPreferencesDataStore
import com.guleryigitcan.teknasyoncase.domain.repository.EarthquakeRepository
import com.guleryigitcan.teknasyoncase.domain.repository.AlertProcessingRepository
import com.guleryigitcan.teknasyoncase.domain.repository.NotificationPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindEarthquakeRepository(impl: EarthquakeRepositoryImpl): EarthquakeRepository

    @Binds
    @Singleton
    abstract fun bindAlertProcessingRepository(impl: AlertProcessingRepositoryImpl): AlertProcessingRepository

    @Binds
    @Singleton
    abstract fun bindNotificationPreferencesRepository(
        impl: NotificationPreferencesDataStore,
    ): NotificationPreferencesRepository
}
