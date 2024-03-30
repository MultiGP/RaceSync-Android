package com.multigp.racesync.domain.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Keep
@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey()
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("addressOne")
    val addressOne: String,
    @field:SerializedName("addressTwo")
    val addressTwo: String,
    @field:SerializedName("authType")
    val authType: String,
    @field:SerializedName("chapterCount")
    val chapterCount: Int,
    @field:SerializedName("chapterIds")
    val chapterIds: List<String>,
    @field:SerializedName("city")
    val city: String,
    @field:SerializedName("country")
    val country: String,
    @field:SerializedName("dateAdded")
    val dateAdded: String,
    @field:SerializedName("dateModified")
    val dateModified: String,
    @field:SerializedName("displayName")
    val displayName: String,
    @field:SerializedName("firstName")
    val firstName: String,
    @field:SerializedName("homeChapterId")
    val homeChapterId: String,
    @field:SerializedName("isPublic")
    val isPublic: Boolean,
    @field:SerializedName("language")
    val language: String,
    @field:SerializedName("lastName")
    val lastName: String,
    @field:SerializedName("latitude")
    val latitude: String,
    @field:SerializedName("longitude")
    val longitude: String,
    @field:SerializedName("phoneNumber")
    val phoneNumber: String,
    @field:SerializedName("profileBackgroundUrl")
    val profileBackgroundUrl: String,
    @field:SerializedName("profilePictureUrl")
    val profilePictureUrl: String,
    @field:SerializedName("raceCount")
    val raceCount: Int,
    @field:SerializedName("state")
    val state: String,
    @field:SerializedName("userName")
    val userName: String,
    @field:SerializedName("zip")
    val zip: String
)