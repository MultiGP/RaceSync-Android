package com.multigp.racesync.domain.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey()
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("address")
    val address: String?,
    @field:SerializedName("city")
    val city: String?,
    @field:SerializedName("country")
    val country: String?,
    @field:SerializedName("dateAdded")
    val dateAdded: String?,
    @field:SerializedName("dateModified")
    val dateModified: String?,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("facebookUrl")
    val facebookUrl: String?,
    @field:SerializedName("googleUrl")
    val googleUrl: String?,
    @field:SerializedName("instagramUrl")
    val instagramUrl: String?,
    @field:SerializedName("latitude")
    val latitude: Double?,
    @field:SerializedName("longitude")
    val longitude: Double?,
    @field:SerializedName("mainImageFileName")
    val mainImageFileName: String?,
    @field:SerializedName("backgroundFileName")
    val backgroundFileName: String?,
    @field:SerializedName("meetupUrl")
    val meetupUrl: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("ownerId")
    val ownerId: String?,
    @field:SerializedName("ownerUserName")
    val ownerUserName: String?,
    @field:SerializedName("state")
    val state: String?,
    @field:SerializedName("tier")
    val tier: String?,
    @field:SerializedName("twitterUrl")
    val twitterUrl: String?,
    @field:SerializedName("url")
    val url: String?,
    @field:SerializedName("urlName")
    val urlName: String?,
    @field:SerializedName("youtubeUrl")
    val youtubeUrl: String?,
    @field:SerializedName("zip")
    val zip: String?,
    @field:SerializedName("raceCount")
    val raceCount: Int?,
    @field:SerializedName("memberCount")
    val memberCount: Int?,
    @field:SerializedName("isJoined")
    val isJoined: Boolean = false,
): Serializable {
    companion object {
        val testObject: Chapter
            get() {
                val chapterData =
                    "{\"id\":\"2057\",\"name\":\"Underground.KL FPV Drone Racing\",\"urlName\":\"Underground.KL-FPV-Drone-Racing\",\"requestedName\":\"\",\"description\":\"Community driven FPV drone racing group based in KL \\/ Selangor, Malaysia, \",\"type\":\"1\",\"mainImageFileName\":\"https:\\/\\/multigp-storage-new.s3.us-east-2.amazonaws.com\\/chapter\\/2057\\/mainImage-766.png\",\"backgroundFileName\":\"backgroundImage-724.png\",\"url\":\"\",\"facebookUrl\":\"https:\\/\\/www.facebook.com\\/groups\\/405213875212259\",\"googleUrl\":\"\",\"twitterUrl\":\"\",\"youtubeUrl\":\"\",\"instagramUrl\":\"\",\"meetupUrl\":\"\",\"latitude\":\"2.9843828\",\"longitude\":\"101.5675132\",\"ownerId\":\"35533\",\"address\":\"PH FPV Drone Racing Track\",\"phone\":\"+60 12-221 8454\",\"city\":\"Selangor\",\"state\":\"Kuala Lumpur\",\"zip\":\"47180\",\"country\":\"MY\",\"isApproved\":\"1\",\"tier\":\"5\",\"division\":null,\"league\":\"0\",\"isPro\":\"0\",\"dateAdded\":\"2024-02-12 08:15:54\",\"dateModified\":null,\"ownerId2\":null,\"ownerId3\":null,\"raceEntryCount\":\"21\",\"raceCount\":\"2\",\"memberCount\":\"21\",\"isJoined\":true,\"ownerUserName\":\"Barracuda\"}"
                return Gson().fromJson(chapterData, Chapter::class.java)
            }
    }

    fun getFormattedAddress(): String {
        val components = mutableListOf<String>()
        city?.let { components.add(it) }
        state?.let { components.add(it) }
        return components.joinToString(separator = ", ")
    }
}