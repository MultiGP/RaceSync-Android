package com.multigp.racesync.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.multigp.racesync.domain.model.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(profile: Profile)

    @Query("SELECT * FROM profiles")
    fun getAll(): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE userName = :userName")
    fun get(userName: String): Flow<Profile>

    @Query("DELETE FROM profiles")
    suspend fun deleteAll()
}