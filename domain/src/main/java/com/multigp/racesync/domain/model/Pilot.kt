package com.multigp.racesync.domain.model
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Keep
@Entity(tableName = "pilots")
data class Pilot(
    @PrimaryKey()
    @field:SerializedName("pilotId")
    val pilotId: String,
    @field:SerializedName("firstName")
    val firstName: String,
    @field:SerializedName("lastName")
    val lastName: String,
    @field:SerializedName("userName")
    val userName: String
)