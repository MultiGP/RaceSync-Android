package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileRequest (
    @SerializedName("apiKey")
    val apiKey: String,
    @SerializedName("sessionId")
    val sessionId: String?
) : Serializable