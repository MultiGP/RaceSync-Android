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
    @field:SerializedName("startDate")
    val startDate: Date? = null,
    @field:SerializedName("endDate")
    val endDate: Date? = null,
    @field:SerializedName("type")
    val scoreType: String? = null,
    @field:SerializedName("mainImageUrl")
    val mainImageUrl: String? = null,
    @field:SerializedName("isJoined")
    var isJoined: Boolean = false,
    @field:SerializedName("pilotCount")
    val pilotCount: Int = 0
) : Serializable {

    val isRegional: Boolean
        get() = scoreType == SCORE_TYPE_REGIONAL

    companion object {
        // MultiGP SeriesScore enum: "4" = regionals.
        const val SCORE_TYPE_REGIONAL = "4"
    }
}
