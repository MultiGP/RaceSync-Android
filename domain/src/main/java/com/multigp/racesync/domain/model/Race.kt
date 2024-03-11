package com.multigp.racesync.domain.model

import android.location.Location
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.multigp.racesync.domain.extensions.formatDate
import com.multigp.racesync.domain.extensions.isWithInRadius
import com.multigp.racesync.domain.extensions.toDate
import java.io.Serializable
import java.util.Date


@Keep
@Entity(tableName = "races")
data class Race(
    @PrimaryKey()
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("counter")
    val counter: Int = 0,
    @field:SerializedName("address")
    val address: String?,
    @field:SerializedName("batteryRestriction")
    val batteryRestriction: String?,
    @field:SerializedName("chapterId")
    val chapterId: String?,
    @field:SerializedName("chapterImageFileName")
    val chapterImageFileName: String?,
    @field:SerializedName("chapterName")
    val chapterName: String,
    @field:SerializedName("city")
    val city: String?,
    @field:SerializedName("content")
    val content: String?,
    @field:SerializedName("country")
    val country: String?,
    @field:SerializedName("courseId")
    val courseId: String?,
    @field:SerializedName("courseName")
    val courseName: String?,
    @field:SerializedName("dateAdded")
    val dateAdded: String?,
    @field:SerializedName("dateModified")
    val dateModified: String?,
    @field:SerializedName("deleteAuth")
    val deleteAuth: String?,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("latitude")
    val latitude: Double?,
    @field:SerializedName("longitude")
    val longitude: Double?,
    @field:SerializedName("mainImageFileName")
    val mainImageFileName: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("ownerId")
    val ownerId: String?,
    @field:SerializedName("ownerUserName")
    val ownerUserName: String?,
    @field:SerializedName("participantCount")
    var participantCount: Int = 0,
    @field:SerializedName("propellerSizeRestriction")
    val propellerSizeRestriction: String?,
    @field:SerializedName("raceClass")
    val raceClass: String?,
    @field:SerializedName("raceClassString")
    val raceClassString: String?,
    @field:SerializedName("raceEntryCount")
    val raceEntryCount: String?,
    @field:SerializedName("scoringDisabled")
    val scoringDisabled: String?,
    @field:SerializedName("seasonId")
    val seasonId: String?,
    @field:SerializedName("seasonName")
    val seasonName: String?,
    @field:SerializedName("sizeRestriction")
    val sizeRestriction: String?,
    @field:SerializedName("startDate")
    val startDate: String?,
    @field:SerializedName("endDate")
    val endDate: String?,
    @field:SerializedName("state")
    val state: String?,
    @field:SerializedName("status")
    val status: String?,
    @field:SerializedName("typeRestriction")
    val typeRestriction: String?,
    @field:SerializedName("updateAuth")
    val updateAuth: String?,
    @field:SerializedName("url")
    val url: String?,
    @field:SerializedName("urlName")
    val urlName: String?,
    @field:SerializedName("zip")
    val zip: String?,
    @field:SerializedName("childRaceCount")
    val childRaceCount: String?,
    @field:SerializedName("isJoined")
    var isJoined: Boolean = false
) : Serializable {

    val location: LatLng
        get() = LatLng(latitude ?: 0.0, longitude ?: 0.0)

    fun getFormattedAddress(): String {
        val components = mutableListOf<String>()
        address?.let { components.add(it) }
        city?.let { components.add(it) }
        state?.let { components.add(it) }
        zip?.let { components.add(it) }
        country?.let { components.add(it) }
        return components.joinToString(separator = ", ")
    }

    val snippet: String
        get() = "Race will be held at ${startDate?.toDate()?.formatDate()}"

    val isUpcoming: Boolean
        get() = (startDate?.toDate()?.compareTo(Date()) ?: -1) >= 0

    fun isWithInSearchRadius(curLocation: Location, radius: Double): Boolean {
        return if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
                .isWithInRadius(LatLng(curLocation.latitude, curLocation.longitude), radius)
        } else {
            false
        }
    }

    companion object {
        val testObject: Race
            get() {
                val raceData =
                    "{\"status\":true,\"data\":[{\"id\":\"26217\",\"name\":\"February eTeam race #2\",\"urlName\":\"February-eTeam-race-2\",\"description\":\"Open class indoor racing at eTeam Hobbyplex in north Fort Collins! All ages are welcome, so bring the family!\",\"type\":\"1\",\"raceType\":\"0\",\"scoringFormat\":\"6\",\"mainImageFileName\":null,\"content\":\"<hr \\/><div class=\\\"race-content\\\">\\n<p>Big-room racing at eTeam, where RC cars have been competing for years.<\\/p>\\n<p>We'll be allowing open-class racing, with no limit on battery sizes, etc; props must be 40mm diameter or less, and must be enclosed in ducts; the overall weight must be under 40 grams. The quad must be deemed to be indoor-safe by our admins.<\\/p>\\n<p>We'll have staff available prior to the race to help people with any problems\\/questions\\/needs, and if you show up early enough, it's possible that you can get some flights lessons as well.<\\/p>\\n<p>Come on down to eTeam Hobbyplex, 1304 Duff Dr. Unit #8 and get some airtime!<\\/p>\\n<\\/div>\",\"itineraryContent\":\"<hr \\/><div class=\\\"race-content\\\">\\n<p>6:00pm-8:30pm - Qualifying<\\/p>\\n<p>8:30-9:00 - Mains (with a podium)<\\/p>\\n<p>9:00-9:30 - Teardown (thanks for helping!)<\\/p>\\n<\\/div>\",\"url\":\"\",\"ownerId\":\"10559\",\"courseId\":\"2508\",\"seasonId\":\"2374\",\"chapterId\":\"1189\",\"parentRaceId\":null,\"nextRaceId\":null,\"nextRaceCopyPilotsType\":null,\"nextRaceCopyPilotsValue\":null,\"nextRaceCopyPilotScores\":null,\"nextRaceCopyPilotTimes\":null,\"startDate\":\"2024-02-20 05:30 pm\",\"status\":\"Open\",\"officialStatus\":\"0\",\"typeRestriction\":null,\"sizeRestriction\":null,\"batteryRestriction\":null,\"propellerSizeRestriction\":null,\"videoTransmitterRestriction\":null,\"scoringDisabled\":false,\"captureTimeEnabled\":\"0\",\"cycleCount\":\"5\",\"maxZippyqDepth\":\"5\",\"zippyqIterator\":\"0\",\"maxBatteriesForQualifying\":null,\"currentCycle\":null,\"currentHeat\":null,\"targetTime\":null,\"slotLayout\":\"7\",\"slotAssignment\":\"3\",\"disabledSlots\":\"\",\"antennaPolarization\":\"0\",\"autofillSlotChangeBetweenCycles\":\"0\",\"disableSlotAutoPopulation\":\"0\",\"latitude\":\"40.591\",\"longitude\":\"-105.052\",\"address\":\"1304 Duff Dr #8\",\"city\":\"Fort Collins\",\"state\":\"CO\",\"zip\":\"80524\",\"country\":\"US\",\"dateAdded\":\"2023-11-03 02:07:22\",\"dateModified\":\"2023-11-03 02:18:23\",\"liveTimeEventUrl\":null,\"registrationRaceId\":null,\"raceClass\":\"1\",\"endDate\":\"2024-02-20 09:30 pm\",\"zippyNoKiosk\":null,\"updateAuth\":false,\"deleteAuth\":false,\"raceEntryCount\":\"3\",\"childRaceCount\":0,\"participantCount\":\"3\",\"isJoined\":false,\"ownerUserName\":\"Slippy\",\"seasonName\":\"2023-24 Whoop Season\",\"courseName\":\"E-Team Hobbyplex\",\"raceClassString\":\"Tiny Whoop\",\"chapterName\":\"FoCo FPV \",\"chapterImageFileName\":\"https:\\/\\/multigp-storage-new.s3.us-east-2.amazonaws.com\\/chapter\\/1189\\/mainImage-119.png\",\"races\":[]}]}"
                return Gson().fromJson(raceData, Race::class.java)
            }
    }
}