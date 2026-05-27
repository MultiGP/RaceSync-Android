package com.multigp.racesync.domain.model.io

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/** Olson ID for the event timezone — matches iOS MGPEventTimeZone. */
const val MGP_EVENT_TIMEZONE_ID = "America/Indiana/Indianapolis"

@Keep
data class Event(
    @SerializedName("event")       val name: String? = null,
    @SerializedName("venue")       val venue: String? = null,
    @SerializedName("lastUpdated") val lastUpdated: String? = null,
    @SerializedName("tracks")      val tracks: List<EventTrack> = emptyList(),
    @SerializedName("sessions")    val sessions: List<EventSession> = emptyList()
)

@Keep
data class EventTrack(
    @SerializedName("id")       val id: String? = null,
    @SerializedName("name")     val name: String? = null,
    @SerializedName("location") val location: String? = null
)

// All string fields are nullable because the upstream Apps Script payload sometimes returns
// JSON `null` for them — and Gson bypasses Kotlin's non-null check during deserialization,
// which then crashes data-class `copy()` later. Read sites use `.orEmpty()` to coalesce.
@Keep
data class EventSession(
    @SerializedName("id")        val id: String = "",
    @SerializedName("day")       val dayName: String? = null,
    @SerializedName("activity")  val activity: String? = null,
    @SerializedName("trackId")   val trackId: String? = null,
    @SerializedName("status")    val statusRaw: String? = null,
    @SerializedName("date")      val rawDate: String? = null,
    @SerializedName("startTime") val rawStartTime: String? = null,
    @SerializedName("endTime")   val rawEndTime: String? = null
) {
    val status: EventStatus get() = EventStatus.from(statusRaw)
}

enum class EventStatus(val raw: String) {
    Closed("closed"),
    Scheduled("scheduled");

    companion object {
        fun from(raw: String?): EventStatus =
            entries.firstOrNull { it.raw.equals(raw, ignoreCase = true) } ?: Closed
    }
}
