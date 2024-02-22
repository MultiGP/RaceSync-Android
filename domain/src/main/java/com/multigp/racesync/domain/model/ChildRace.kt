package com.multigp.racesync.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ChildRace(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
):Parcelable