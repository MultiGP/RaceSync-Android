package com.multigp.racesync.domain.model
import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
data class Race(
    @SerializedName("counter")
    val counter: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("batteryRestriction")
    val batteryRestriction: String,
    @SerializedName("chapterId")
    val chapterId: String,
    @SerializedName("chapterImageFileName")
    val chapterImageFileName: String?,
    @SerializedName("chapterName")
    val chapterName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("courseId")
    val courseId: String,
    @SerializedName("courseName")
    val courseName: String,
    @SerializedName("dateAdded")
    val dateAdded: String,
    @SerializedName("dateModified")
    val dateModified: String,
    @SerializedName("deleteAuth")
    val deleteAuth: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("mainImageFileName")
    val mainImageFileName: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("ownerUserName")
    val ownerUserName: String,
    @SerializedName("participantCount")
    val participantCount: String,
    @SerializedName("propellerSizeRestriction")
    val propellerSizeRestriction: String,
    @SerializedName("raceClass")
    val raceClass: String,
    @SerializedName("raceClassString")
    val raceClassString: String,
    @SerializedName("raceEntryCount")
    val raceEntryCount: String,
    @SerializedName("scoringDisabled")
    val scoringDisabled: String,
    @SerializedName("seasonId")
    val seasonId: String,
    @SerializedName("seasonName")
    val seasonName: String,
    @SerializedName("sizeRestriction")
    val sizeRestriction: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("typeRestriction")
    val typeRestriction: String,
    @SerializedName("updateAuth")
    val updateAuth: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlName")
    val urlName: String,
    @SerializedName("zip")
    val zip: String,
    @SerializedName("childRaceCount")
    val childRaceCount: String,
    @SerializedName("childRaces")
    val childRaces: List<ChildRace>
)