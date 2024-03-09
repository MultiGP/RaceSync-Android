package com.multigp.racesync.domain.model
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName


@Keep
@Entity(tableName = "aircrafts")
data class Aircraft(
    @PrimaryKey()
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("antenna")
    val antenna: String?,
    @field:SerializedName("antennaId")
    val antennaId: String?,
    @field:SerializedName("backgroundFileName")
    val backgroundFileName: String?,
    @field:SerializedName("battery")
    val battery: String?,
    @field:SerializedName("dateAdded")
    val dateAdded: String?,
    @field:SerializedName("dateModified")
    val dateModified: String?,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("electronicSpeedControllerId")
    val electronicSpeedControllerId: String?,
    @field:SerializedName("entryCount")
    val entryCount: String?,
    @field:SerializedName("flightControllerId")
    val flightControllerId: String?,
    @field:SerializedName("fpvCameraId")
    val fpvCameraId: String?,
    @field:SerializedName("fpvCameraLenseId")
    val fpvCameraLenseId: String?,
    @field:SerializedName("frameId")
    val frameId: String?,
    @field:SerializedName("mainImageFileName")
    val mainImageFileName: String?,
    @field:SerializedName("motorId")
    val motorId: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("propellerId")
    val propellerId: String?,
    @field:SerializedName("propellerSize")
    val propellerSize: String?,
    @field:SerializedName("retired")
    val retired: String?,
    @field:SerializedName("scannableId")
    val scannableId: String?,
    @field:SerializedName("size")
    val size: String?,
    @field:SerializedName("type")
    val type: String?,
    @field:SerializedName("urlName")
    val urlName: String?,
    @field:SerializedName("videoReceiverChannels")
    val videoReceiverChannels: String?,
    @field:SerializedName("videoTransmitter")
    val videoTransmitter: String?,
    @field:SerializedName("videoTransmitterChannels")
    val videoTransmitterChannels: String?,
    @field:SerializedName("videoTransmitterPower")
    val videoTransmitterPower: String?,
    @field:SerializedName("wingSize")
    val wingSize: Double?
)