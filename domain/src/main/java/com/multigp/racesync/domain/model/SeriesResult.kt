package com.multigp.racesync.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class SeriesResultType { Pilot, Chapter }

@Keep
data class SeriesResult(
    @field:SerializedName("pilotId")
    val pilotId: String? = null,
    @field:SerializedName("chapterId")
    val chapterId: String? = null,

    /** "userName" in the API; falls back to "displayName" / "chapterName" via [resolvedDisplayName]. */
    @field:SerializedName("userName")
    val userName: String? = null,
    @field:SerializedName("displayName")
    val displayName: String? = null,
    @field:SerializedName("chapterName")
    val chapterName: String? = null,
    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("score")
    val score: Double = 0.0,
    @field:SerializedName("eloScore")
    val eloScore: Int = 0,
    @field:SerializedName("raceCount")
    val raceCount: Int = 0,

    /** Fastest 3-lap time, in seconds (string in the API). Null when not applicable. */
    @field:SerializedName("fastest3Laps")
    val fastest3Laps: String? = null,

    /** Best individual lap times — used by collegiate chapter rows. */
    @field:SerializedName("bestResults")
    val bestResults: List<Double>? = null,

    /** Pilot avatar. Falls back to [mainImageUrl] when absent. */
    @field:SerializedName("profilePictureUrl")
    val profileImageUrl: String? = null,
    /** Chapter / fallback image. */
    @field:SerializedName("mainImageFileName")
    val mainImageUrl: String? = null
) : Serializable {

    val type: SeriesResultType
        get() = if (pilotId != null) SeriesResultType.Pilot else SeriesResultType.Chapter

    /** Mirrors iOS `displayName` resolution: userName → displayName → chapterName. */
    val resolvedDisplayName: String
        get() = userName?.takeIf { it.isNotBlank() }
            ?: displayName?.takeIf { it.isNotBlank() }
            ?: chapterName.orEmpty()

    /** Pilots prefer their profile picture; otherwise fall back to the chapter / main image. */
    val avatarUrl: String?
        get() = profileImageUrl ?: mainImageUrl
}
