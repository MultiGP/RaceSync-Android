package com.multigp.racesync.domain.model.requests
import com.google.gson.annotations.SerializedName


data class JoinRaceRequest(
    @SerializedName("aircraftId")
    val aircraftId: String
)