package com.multigp.racesync.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.ChapterRemoteKeys
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceRemoteKeys

@Database(
    entities = [Race::class, Chapter::class, RaceRemoteKeys::class, ChapterRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class RaceSyncDB : RoomDatabase() {
    abstract fun raceDao(): RaceDao
    abstract fun raceRemoteKeysDao(): RaceRemoteKeysDao

    abstract fun chapterDao(): ChapterDao
    abstract fun chapterRemoteKeysDao(): ChapterRemoteKeysDao
}