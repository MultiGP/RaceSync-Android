package com.multigp.racesync.domain.model.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateFCMTokenRequest (
    @SerializedName("data")
    val data: SaveFCMTokenData
): Parcelable

@Parcelize
class SaveFCMTokenData (
    @SerializedName("action")
    val action: String,
    @SerializedName("devicetoken")
    val devicetoken: String
): Parcelable
