package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName

data class SearchRequest(
    @SerializedName("userName")
    val userName: String
)
