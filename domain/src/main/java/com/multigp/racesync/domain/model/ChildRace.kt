package com.multigp.racesync.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChildRace(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)