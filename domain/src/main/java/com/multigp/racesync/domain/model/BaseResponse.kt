package com.multigp.racesync.domain.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("data")
    val data: T?,
    @SerializedName("errors")
    val errors: Error? = null,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("statusDescription")
    val statusDescription: String
) {
    fun errorMessage() = errors?.password?.get(0) ?: ""
}
