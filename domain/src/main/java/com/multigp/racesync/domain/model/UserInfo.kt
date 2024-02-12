package com.multigp.racesync.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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