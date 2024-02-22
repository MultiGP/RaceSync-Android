package com.multigp.racesync.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "race_remote_keys")
class RaceRemoteKeys(
    @PrimaryKey
    val raceId: String,
    val prevKey: Int?,
    val nextKey: Int?
)