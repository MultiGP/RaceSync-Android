package com.multigp.racesync.domain.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import retrofit2.Response

data class BaseResponse<T>(
    @SerializedName("data")
    val data: T?,
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = emptyMap(),
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("statusDescription")
    val statusDescription: String?
) {
    fun errorMessage(): String {
       return errors?.values?.joinToString("\n") ?: statusDescription ?: "Unknown Error"
    }

    companion object {
        fun <T>convertFromErrorResponse(response: Response<BaseResponse<T>>): BaseResponse<T> {
            val gson = Gson()
            val errorBody = response.errorBody()?.string()
            val type = object : TypeToken<BaseResponse<T>>() {}.type
            return gson.fromJson(errorBody, type)
        }
    }
}
