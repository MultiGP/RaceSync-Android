package com.multigp.racesync.domain.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class LoginRequest(
    @SerializedName("apiKey")
    val apiKey: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
) : Serializable