package com.multigp.racesync.di

import android.app.Application
import androidx.room.Room
import com.multigp.racesync.data.db.AircraftDao
import com.multigp.racesync.data.db.ChapterDao
import com.multigp.racesync.data.db.ChapterRemoteKeysDao
import com.multigp.racesync.data.db.RaceDao
import com.multigp.racesync.data.db.RaceRemoteKeysDao
import com.multigp.racesync.data.db.RaceSyncDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(app: Application): RaceSyncDB =
        Room.databaseBuilder(app, RaceSyncDB::class.java, "race_sync_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRaceDao(raceSyncDB: RaceSyncDB): RaceDao = raceSyncDB.raceDao()

    @Provides
    fun provideRaceRemoteKeysDao(raceSyncDB: RaceSyncDB): RaceRemoteKeysDao =
        raceSyncDB.raceRemoteKeysDao()

    @Provides
    fun provideChapterDao(raceSyncDB: RaceSyncDB): ChapterDao = raceSyncDB.chapterDao()

    @Provides
    fun provideChapterRemoteKeysDao(raceSyncDB: RaceSyncDB): ChapterRemoteKeysDao =
        raceSyncDB.chapterRemoteKeysDao()

    @Provides
    fun provideAircraftDao(raceSyncDB: RaceSyncDB): AircraftDao = raceSyncDB.aircraftDao()
}