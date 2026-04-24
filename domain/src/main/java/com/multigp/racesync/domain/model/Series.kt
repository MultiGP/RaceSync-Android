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
    @field:SerializedName("mainImageUrl")
    val mainImageUrl: String? = null,
    @field:SerializedName("primary_color")
    val color: String? = null,
    @field:SerializedName("isJoined")
    var isJoined: Boolean = false,
    @field:SerializedName("pilotCount")
    val pilotCount: Int = 0,
    @field:SerializedName("raceCount")
    val raceCount: Int = 0,
    @field:SerializedName("raceApprovedCount")
    val raceApprovedCount: Int = 0,
    @field:SerializedName("chapterCount")
    val chapterCount: Int = 0
) : Serializable {

    val isRegional: Boolean
        get() = scoreType == SCORE_TYPE_REGIONAL

    companion object {
        // MultiGP SeriesScore enum: "4" = regionals.
        const val SCORE_TYPE_REGIONAL = "4"
    }
}

/**
 * Parses a hex color string (e.g. `"#AABBCC"`, `"AABBCC"`, `"#AARRGGBB"`) into an ARGB int,
 * or returns null if the input is blank/malformed. Safe to call with any API-supplied value.
 */
fun parseHexColorOrNull(hex: String?): Int? {
    if (hex.isNullOrBlank()) return null
    val cleaned = hex.trim().removePrefix("#")
    val value = cleaned.toLongOrNull(radix = 16) ?: return null
    return when (cleaned.length) {
        6 -> (0xFF000000L or value).toInt()
        8 -> value.toInt()
        else -> null
    }
}
