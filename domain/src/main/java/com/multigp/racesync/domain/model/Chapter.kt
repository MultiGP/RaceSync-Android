package com.multigp.racesync.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Chapter(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("dateAdded")
    val dateAdded: String,
    @SerializedName("dateModified")
    val dateModified: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("facebookUrl")
    val facebookUrl: String,
    @SerializedName("googleUrl")
    val googleUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("instagramUrl")
    val instagramUrl: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("mainImageFileName")
    val mainImageFileName: String?,
    @SerializedName("meetupUrl")
    val meetupUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("ownerUserName")
    val ownerUserName: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("tier")
    val tier: String,
    @SerializedName("twitterUrl")
    val twitterUrl: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlName")
    val urlName: String,
    @SerializedName("youtubeUrl")
    val youtubeUrl: String,
    @SerializedName("zip")
    val zip: String,
    @SerializedName("memberCount")
    val memberCount: Int,
    @SerializedName("isJoined")
    val isJoined: Boolean,
) : Parcelable