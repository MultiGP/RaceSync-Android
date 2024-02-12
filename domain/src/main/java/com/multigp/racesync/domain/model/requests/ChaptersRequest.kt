package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName

data class ChaptersRequest(
    @SerializedName("joined")
    val joined: JoinedChapters? = null,
    @SerializedName("nearBy")
    val nearBy: NearbyChapters? = null,
)

data class JoinedChapters(
    @SerializedName("pilotId")
    val pilotId: String
)

data class NearbyChapters(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("radius")
    val radius: Double
)

