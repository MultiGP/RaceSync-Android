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
    val addressOne: String? = null,
    @field:SerializedName("addressTwo")
    val addressTwo: String? = null,
    @field:SerializedName("authType")
    val authType: String? = null,
    @field:SerializedName("chapterCount")
    val chapterCount: Int = 0,
    @field:SerializedName("chapterIds")
    val chapterIds: List<String> = emptyList(),
    @field:SerializedName("city")
    val city: String? = null,
    @field:SerializedName("country")
    val country: String? = null,
    @field:SerializedName("dateAdded")
    val dateAdded: String? = null,
    @field:SerializedName("dateModified")
    val dateModified: String? = null,
    @field:SerializedName("displayName")
    val displayName: String? = null,
    @field:SerializedName("firstName")
    val firstName: String? = null,
    @field:SerializedName("homeChapterId")
    val homeChapterId: String? = null,
    @field:SerializedName("isPublic")
    val isPublic: Boolean = false,
    @field:SerializedName("language")
    val language: String? = null,
    @field:SerializedName("lastName")
    val lastName: String? = null,
    @field:SerializedName("latitude")
    val latitude: Double? = null,
    @field:SerializedName("longitude")
    val longitude: Double? = null,
    @field:SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @field:SerializedName("profileBackgroundUrl")
    val profileBackgroundUrl: String? = null,
    @field:SerializedName("profilePictureUrl")
    val profilePictureUrl: String? = null,
    @field:SerializedName("raceCount")
    val raceCount: Int? = 0,
    @field:SerializedName("state")
    val state: String? = "",
    @field:SerializedName("userName")
    val userName: String,
    @field:SerializedName("zip")
    val zip: String? = null
){
    fun getFormattedAddress(): String {
        val components = mutableListOf<String>()
        city?.let { components.add(it) }
        state?.let { components.add(it) }
        return components.joinToString(separator = ", ")
    }
}
