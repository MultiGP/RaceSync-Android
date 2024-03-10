package com.multigp.racesync.domain.model.requests
import com.google.gson.annotations.SerializedName


data class JoinRaceRequest(
    @SerializedName("aircraftId")
    val aircraftId: Int,
    @SerializedName("pilotId")
    val pilotId: Int
)