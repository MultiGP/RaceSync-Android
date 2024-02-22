package com.multigp.racesync.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.multigp.racesync.domain.model.RaceRemoteKeys

@Dao
interface RaceRemoteKeysDao {
    @Query("SELECT * FROM race_remote_keys WHERE raceId = :raceId")
    suspend fun getRaceRemoteKeys(raceId: String): RaceRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRaceRemoteKeys(raceRemoteKeys: List<RaceRemoteKeys>)

    @Query("DELETE FROM race_remote_keys")
    suspend fun deleteAllRaceRemoteKeys()
}