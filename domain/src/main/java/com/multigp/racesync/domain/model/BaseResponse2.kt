package com.multigp.racesync.domain.model
import com.google.gson.annotations.SerializedName


data class BaseResponse2<T>(
    @SerializedName("data")
    val data: T,
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = emptyMap(),
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("statusDescription")
    val statusDescription: String
){
    fun errorMessage(): String{
        return errors?.values?.joinToString("\n") ?: "Unknown Error"
    }
}