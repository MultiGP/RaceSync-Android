package com.multigp.racesync.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.multigp.racesync.domain.model.Race
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRaces(races: List<Race>)

    @Query("SELECT * FROM races")
    fun getAllRaces(): PagingSource<Int, Race>

    @Query("SELECT * FROM races WHERE id = :raceId")
    fun getRace(raceId: String): Flow<Race>

    @Query("SELECT * FROM races WHERE isJoined = :isJoined")
    fun getJoinedRaces(isJoined: Boolean): PagingSource<Int, Race>

    @Query("DELETE FROM races")
    suspend fun deleteAllRaces()

    @Update
    suspend fun updateRace(race:Race)
}