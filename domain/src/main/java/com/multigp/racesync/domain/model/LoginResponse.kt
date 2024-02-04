package com.multigp.racesync.domain.model
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class LoginResponse(
    @SerializedName("data")
    val userInfo: UserInfo,
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


@Parcelize
data class UserInfo(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("userName")
    val userName: String
): Parcelable

data class Error(
    @SerializedName("password")
    val password: List<String>
)