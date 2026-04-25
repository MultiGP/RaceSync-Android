package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName

/** Body for series/race approver actions: `{ "raceId": "..." }`. */
data class RaceIdRequest(
    @SerializedName("raceId")
    val raceId: String
)
