package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName

data class RaceRequest(
    @SerializedName("joined")
    val joined: JoinedRaces? = null,
    @SerializedName("nearBy")
    val nearBy: NearbyRaces? = null,
    @SerializedName("upcoming")
    val upComing: UpcomingRaces? = null,
    @SerializedName("past")
    val past: PastRaces? = null,
    @SerializedName("name")
    val name:String? = null,
    @SerializedName("id")
    val id:String? = null,
    @SerializedName("chapterId")
    val chapterId:List<String>? = null
)

data class JoinedRaces(
    @SerializedName("pilotId")
    val pilotId: String
)

data class NearbyRaces(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("radius")
    val radius: Double
)

data class UpcomingRaces(
    @SerializedName("limit")
    val limit: Int = 100,
    @SerializedName("orderByDistance")
    val orderByDistance: Boolean = true
)


data class PastRaces(
    @SerializedName("limit")
    val limit: Int = 100,
    @SerializedName("orderByDistance")
    val orderByDistance: Boolean = true
)