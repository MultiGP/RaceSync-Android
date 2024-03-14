package com.multigp.racesync.domain.extensions

import com.google.android.gms.maps.model.LatLng

fun LatLng.isWithInRadius(other: LatLng, radius: Double): Boolean {
    val distance = this.calculateDistance(other)
    return  distance <= radius
}

fun LatLng.calculateDistance(other: LatLng): Double {
    val R = 6371
    val dLat = Math.toRadians(other.latitude - this.latitude)
    val dLon = Math.toRadians(other.longitude - this.latitude)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}

