package com.guleryigitcan.teknasyoncase.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.alertPreferencesDataStore by preferencesDataStore(name = "alert_settings")

@Module
@InstallIn(SingletonComponent::class)
internal object PreferencesModule {
    @Provides
    @Singleton
    fun provideAlertPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.alertPreferencesDataStore
}
