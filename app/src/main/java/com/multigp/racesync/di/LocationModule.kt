package com.multigp.racesync.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.multigp.racesync.data.location.FusedLocationProvider
import com.multigp.racesync.domain.location.LocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Singleton
    @Provides
    fun provideLocationProvider(
        locationClient: FusedLocationProviderClient
    ): LocationProvider {
        return FusedLocationProvider(locationClient)
    }
}