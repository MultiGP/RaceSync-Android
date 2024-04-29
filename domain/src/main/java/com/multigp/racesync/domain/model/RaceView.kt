package com.multigp.racesync.domain.model

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName


@Keep
data class RaceView(
    @SerializedName("address")
    val address: String,
    @SerializedName("antennaPolarization")
    val antennaPolarization: String,
    @SerializedName("autofillSlotChangeBetweenCycles")
    val autofillSlotChangeBetweenCycles: String,
    @SerializedName("batteryRestriction")
    val batteryRestriction: Any,
    @SerializedName("captureTimeEnabled")
    val captureTimeEnabled: String,
    @SerializedName("chapterId")
    val chapterId: String,
    @SerializedName("chapterImageFileName")
    val chapterImageFileName: String,
    @SerializedName("chapterName")
    val chapterName: String,
    @SerializedName("childRaceCount")
    val childRaceCount: Int,
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
    @SerializedName("currentCycle")
    val currentCycle: Any,
    @SerializedName("currentHeat")
    val currentHeat: Any,
    @SerializedName("cycleCount")
    val cycleCount: String,
    @SerializedName("dateAdded")
    val dateAdded: String,
    @SerializedName("dateModified")
    val dateModified: String,
    @SerializedName("deleteAuth")
    val deleteAuth: Boolean,
    @SerializedName("description")
    val description: String,
    @SerializedName("disableSlotAutoPopulation")
    val disableSlotAutoPopulation: String,
    @SerializedName("disabledSlots")
    val disabledSlots: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("entries")
    val entries: List<RaceEntry>,
    @SerializedName("itineraryContent")
    val itineraryContent: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("liveTimeEventUrl")
    val liveTimeEventUrl: Any,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("mainImageFileName")
    val mainImageFileName: Any,
    @SerializedName("maxBatteriesForQualifying")
    val maxBatteriesForQualifying: Any,
    @SerializedName("maxZippyqDepth")
    val maxZippyqDepth: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nextRaceCopyPilotScores")
    val nextRaceCopyPilotScores: Any,
    @SerializedName("nextRaceCopyPilotTimes")
    val nextRaceCopyPilotTimes: Any,
    @SerializedName("nextRaceCopyPilotsType")
    val nextRaceCopyPilotsType: Any,
    @SerializedName("nextRaceCopyPilotsValue")
    val nextRaceCopyPilotsValue: Any,
    @SerializedName("nextRaceId")
    val nextRaceId: Any,
    @SerializedName("officialStatus")
    val officialStatus: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("parentRaceId")
    val parentRaceId: Any,
    @SerializedName("participantCount")
    val participantCount: String,
    @SerializedName("propellerSizeRestriction")
    val propellerSizeRestriction: Any,
    @SerializedName("raceClass")
    val raceClass: String,
    @SerializedName("raceClassString")
    val raceClassString: String,
    @SerializedName("raceEntryCount")
    val raceEntryCount: String,
    @SerializedName("raceType")
    val raceType: String,
    @SerializedName("races")
    val races: List<Any>,
    @SerializedName("registrationRaceId")
    val registrationRaceId: Any,
    @SerializedName("schedule")
    val schedule: Any,
    @SerializedName("scoringDisabled")
    val scoringDisabled: String,
    @SerializedName("scoringFormat")
    val scoringFormat: String,
    @SerializedName("seasonId")
    val seasonId: Any,
    @SerializedName("sizeRestriction")
    val sizeRestriction: Any,
    @SerializedName("slotAssignment")
    val slotAssignment: String,
    @SerializedName("slotLayout")
    val slotLayout: String,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("targetTime")
    val targetTime: Any,
    @SerializedName("typeRestriction")
    val typeRestriction: Any,
    @SerializedName("updateAuth")
    val updateAuth: Boolean,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlName")
    val urlName: String,
    @SerializedName("videoTransmitterRestriction")
    val videoTransmitterRestriction: Any,
    @SerializedName("zip")
    val zip: String,
    @SerializedName("zippyNoKiosk")
    val zippyNoKiosk: String,
    @SerializedName("zippyqIterator")
    val zippyqIterator: String
)

@Keep
data class RaceEntry(
    @SerializedName("aircraftId")
    val aircraftId: String?,
    @SerializedName("aircraftName")
    val aircraftName: String?,
    @SerializedName("band")
    val band: String?,
    @SerializedName("channel")
    val channel: String? = "",
    @SerializedName("dateAdded")
    val dateAdded: String?,
    @SerializedName("dateModified")
    val dateModified: String?,
    @SerializedName("displayName")
    val displayName: String?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("frequency")
    val frequency: String? = "",
    @SerializedName("group")
    val group: String?,
    @SerializedName("groupSlot")
    val groupSlot: String,
    @SerializedName("id")
    val id: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("pilotId")
    val pilotId: String?,
    @SerializedName("pilotName")
    val pilotName: String?,
    @SerializedName("pilotUserName")
    val pilotUserName: String,
    @SerializedName("profilePictureUrl")
    val profilePictureUrl: String?,
    @SerializedName("scannableId")
    val scannableId: String?,
    @SerializedName("score")
    val score: Any?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("videoTransmitter")
    val videoTransmitter: String?
) {
    val bandChannel: String
        get() {
            return if (band != null && channel != null) {
                "$band$channel"
            } else {
                frequency ?: ""
            }
        }

    val bandName: String
        get() = if (band != null && band == "R") "Race Band" else (band ?: "—")

    val channelFrequency: String?
        get() {
            return if ((channel ?: "").isEmpty() && (frequency ?: "").isEmpty()) {
                "—"
            } else if ((channel ?: "").isNotEmpty() && (frequency ?: "").isEmpty()) {
                channel
            } else if ((channel ?: "").isEmpty() && (frequency ?: "").isNotEmpty()) {
                "($frequency)"
            } else {
                "$channel($frequency)"
            }
        }

    val color: Color
        get() = when ("$band$channel") {
            "R1" -> Color(0xff00ff15) //Green
            "R2" -> Color(0xff0095ff) //Blue
            "R3" -> Color(0xffe59be9) //pink
            "R4" -> Color(0xffff9500) //Brown
            "R5" -> Color(0xff00ff15) //Green
            "R6" -> Color(0xff6a00ff) //Blue2
            "R7" -> Color(0xffff00ff) //Magenta
            "R8" -> Color(0xffff0040) //Red
            else -> Color.Gray
        }

    val channelTextColor: Color
        get() = when ("$band$channel") {
            "R1", "R5" -> Color.Black
            "R6", "R2", "R3" -> Color.White
            else -> Color.DarkGray
        }
}

@Keep
data class RaceSchedule(
    @SerializedName("rounds")
    val rounds: List<Round>
)

@Keep
data class Round(
    @SerializedName("heats")
    val heats: List<Heat>,
    @SerializedName("name")
    val name: String,
    @SerializedName("roundType")
    val roundType: Int
)

@Keep
data class Heat(
    @SerializedName("entries")
    val entries: List<RaceEntryX>,
    @SerializedName("name")
    val name: String
)

@Keep
data class RaceEntryX(
    @SerializedName("aircraftName")
    val aircraftName: String,
    @SerializedName("band")
    val band: String,
    @SerializedName("channel")
    val channel: String,
    @SerializedName("dateAdded")
    val dateAdded: String,
    @SerializedName("dateModified")
    val dateModified: Any,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("fastest2Laps")
    val fastest2Laps: Any,
    @SerializedName("fastest3Laps")
    val fastest3Laps: Any,
    @SerializedName("fastestLap")
    val fastestLap: Any,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("frequency")
    val frequency: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("pilotId")
    val pilotId: String,
    @SerializedName("pilotName")
    val pilotName: Any,
    @SerializedName("profilePictureUrl")
    val profilePictureUrl: String,
    @SerializedName("raceEntryId")
    val raceEntryId: String,
    @SerializedName("scannableId")
    val scannableId: String,
    @SerializedName("score")
    val score: Any,
    @SerializedName("slot")
    val slot: Int,
    @SerializedName("totalLaps")
    val totalLaps: Any,
    @SerializedName("totalTime")
    val totalTime: Any,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("videoTransmitter")
    val videoTransmitter: Int
)