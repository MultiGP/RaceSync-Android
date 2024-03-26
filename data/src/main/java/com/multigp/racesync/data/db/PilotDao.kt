package com.multigp.racesync.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.multigp.racesync.domain.model.Pilot
import kotlinx.coroutines.flow.Flow

@Dao
interface PilotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(pilots: List<Pilot>)
    @Query("SELECT * FROM pilots")
    fun getAll(): Flow<List<Pilot>>

    @Query("SELECT * FROM pilots WHERE pilotId = :pilotId")
    fun get(pilotId: String): Flow<Pilot>

    @Query("DELETE FROM pilots")
    suspend fun deleteAll()
}