package com.multigp.racesync.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.multigp.racesync.domain.extensions.StringListConverter
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.ChapterRemoteKeys
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceRemoteKeys

@Database(
    entities = [
        Race::class,
        Chapter::class,
        RaceRemoteKeys::class,
        ChapterRemoteKeys::class,
        Aircraft::class,
        Pilot::class,
        Profile::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class RaceSyncDB : RoomDatabase() {
    abstract fun raceDao(): RaceDao
    abstract fun raceRemoteKeysDao(): RaceRemoteKeysDao

    abstract fun chapterDao(): ChapterDao
    abstract fun chapterRemoteKeysDao(): ChapterRemoteKeysDao

    abstract fun aircraftDao(): AircraftDao

    abstract fun pilotDao(): PilotDao

    abstract fun profileDao(): ProfileDao
}