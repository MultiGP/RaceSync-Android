package com.multigp.racesync.domain.model
import com.google.gson.annotations.SerializedName


data class BaseResponse2<T>(
    @SerializedName("data")
    val data: T,
    @SerializedName("errors")
    val errors: Error? = null,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("statusDescription")
    val statusDescription: String
){
    fun errorMessage() = errors?.password?.get(0) ?: ""
}

data class Error(
    @SerializedName("password")
    val password: List<String>
)