package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("apiKey")
    val apiKey: String,
)