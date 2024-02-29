package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AircraftRequest (
    @SerializedName("apiKey")
    val apiKey: String,
    @SerializedName("sessionId")
    val sessionId: String?,
    @SerializedName("data")
    val data: PilotData,
) : Serializable


data class PilotData(
    @SerializedName("pilotId")
    val pilotId: Int,
    val retired: Boolean
)