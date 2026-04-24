package com.multigp.racesync.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

@Keep
data class Series(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String? = null,
    @field:SerializedName("startDate")
    val startDate: Date? = null,
    @field:SerializedName("endDate")
    val endDate: Date? = null,
    @field:SerializedName("type")
    val scoreType: String? = null,
    @field:SerializedName("typeString")
    val scoreTypeString: String? = null,
    @field:SerializedName("approved")
    val isApproved: Boolean = false,
    @field:SerializedName("ownerId")
    val ownerId: String? = null,
    @field:SerializedName("mainImageUrl")
    val mainImageUrl: String? = null,
    @field:SerializedName("primary_color")
    val color: String? = null,
    @field:SerializedName("isJoined")
    var isJoined: Boolean = false,
    @field:SerializedName("pilotCount")
    val pilotCount: Int = 0,
    @field:SerializedName("chapterCount")
    val chapterCount: Int = 0,
    @field:SerializedName("raceCount")
    val raceCount: Int = 0
) : Serializable {

    /** iOS `SeriesScore.regionals` → `"4"`; used for both the Regionals filter and the carousel. */
    val isRegional: Boolean
        get() = scoreType == SCORE_TYPE_REGIONAL

    companion object {
        const val SCORE_TYPE_REGIONAL = "4"
    }
}
