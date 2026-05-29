package com.multigp.racesync.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.data.repository.ChaptersRepositoryImpl
import com.multigp.racesync.data.repository.EventSessionBucketlistImpl
import com.multigp.racesync.data.repository.IoScheduleRepositoryImpl
import com.multigp.racesync.data.repository.LoginRepositoryImpl
import com.multigp.racesync.data.repository.ProfileRepositoryImpl
import com.multigp.racesync.data.repository.RacesRepositoryImpl
import com.multigp.racesync.data.repository.SeriesRepositoryImpl
import com.multigp.racesync.data.repository.StandingsRepositoryImpl
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.repositories.ChaptersRepository
import com.multigp.racesync.domain.repositories.EventSessionBucketlist
import com.multigp.racesync.domain.repositories.IoScheduleRepository
import com.multigp.racesync.domain.repositories.LoginRepository
import com.multigp.racesync.domain.repositories.ProfileRepository
import com.multigp.racesync.domain.repositories.RacesRepository
import com.multigp.racesync.domain.repositories.SeriesRepository
import com.multigp.racesync.domain.repositories.StandingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideLoginRepository(
        onboardingDataSource: OnboardingDataSource,
        dataStore: DataStoreManager,
        apiKey: String,
    ): LoginRepository = LoginRepositoryImpl(onboardingDataSource, dataStore, apiKey)

    @Provides
    fun provideProfileRepository(
        raceSyncApi: RaceSyncApi,
        raceSyncDB: RaceSyncDB,
        dataStore: DataStoreManager,
        apiKey: String,
    ): ProfileRepository = ProfileRepositoryImpl(raceSyncApi, raceSyncDB, dataStore, apiKey)

    @Provides
    fun provideChaptersRepository(
        raceSyncApi: RaceSyncApi,
        raceSyncDB: RaceSyncDB,
        dataStore: DataStoreManager,
        locationClient: FusedLocationProviderClient,
        apiKey: String,
    ): ChaptersRepository =
        ChaptersRepositoryImpl(raceSyncApi, raceSyncDB, locationClient, dataStore, apiKey)

    @Provides
    fun provideRacesRepository(
        raceSyncApi: RaceSyncApi,
        raceSyncDB: RaceSyncDB,
        dataStore: DataStoreManager,
        apiKey: String,
    ): RacesRepository =
        RacesRepositoryImpl(raceSyncApi, raceSyncDB, dataStore, apiKey)

    @Provides
    fun provideStandingsRepository(): StandingsRepository = StandingsRepositoryImpl()

    @Provides
    fun provideSeriesRepository(
        raceSyncApi: RaceSyncApi,
        dataStore: DataStoreManager,
        apiKey: String,
    ): SeriesRepository = SeriesRepositoryImpl(raceSyncApi, dataStore, apiKey)

    @Provides
    @Singleton
    fun provideIoScheduleRepository(
        @ApplicationContext context: Context,
    ): IoScheduleRepository = IoScheduleRepositoryImpl.fromContext(context)

    @Provides
    @Singleton
    fun provideEventSessionBucketlist(
        @ApplicationContext context: Context,
    ): EventSessionBucketlist = EventSessionBucketlistImpl.fromContext(context)
}