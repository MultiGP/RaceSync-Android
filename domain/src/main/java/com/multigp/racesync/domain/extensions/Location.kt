package com.multigp.racesync.domain.extensions

import com.google.android.gms.maps.model.LatLng


private const val EARTH_RADIUS_MILES = 3958

fun LatLng.isWithInRadius(other: LatLng, radius: Double): Boolean {
    val distance = this.calculateDistance(other)
    return  distance <= radius
}

fun LatLng.calculateDistance(other: LatLng): Double {
    val lat1 = Math.toRadians(this.latitude)
    val lon1 = Math.toRadians(this.longitude)
    val lat2 = Math.toRadians(other.latitude)
    val lon2 = Math.toRadians(other.longitude)

    val dLon = lon2 - lon1
    val dLat = lat2 - lat1

    val a = (Math.pow(Math.sin(dLat / 2), 2.0)
            + (Math.cos(lat1) * Math.cos(lat2)
            * Math.pow(Math.sin(dLon / 2), 2.0)))

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return EARTH_RADIUS_MILES * c
}

