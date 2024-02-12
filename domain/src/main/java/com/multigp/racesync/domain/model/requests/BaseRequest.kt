package com.multigp.racesync.domain.model.requests

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseRequest<T>(
    @SerializedName("apiKey")
    val apiKey: String,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("data")
    val data: T? = null
) : Serializable