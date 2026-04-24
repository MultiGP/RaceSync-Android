package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName

data class SeriesRequest(
    @SerializedName("id")
    val id: String? = null
)
