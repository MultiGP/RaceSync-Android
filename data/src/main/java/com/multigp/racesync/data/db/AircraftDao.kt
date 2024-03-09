package com.multigp.racesync.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.multigp.racesync.domain.model.Aircraft
import kotlinx.coroutines.flow.Flow

@Dao
interface AircraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(aircrafts: List<Aircraft>)
    @Query("SELECT * FROM aircrafts")
    fun getAll(): Flow<List<Aircraft>>

    @Query("SELECT * FROM aircrafts WHERE id = :aircraftId")
    fun get(aircraftId: String): Flow<Aircraft>

    @Query("DELETE FROM aircrafts")
    suspend fun deleteAll()
}