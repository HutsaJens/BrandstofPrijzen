package com.example.brandstofprijzen.location

import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.atan2

fun getDistanceFromLatLonInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371 // Radius of the earth in kilometers

    val deltaLatitude = deg2rad(lat2 - lat1)
    val deltaLongitude = deg2rad(lon2 - lon1)

    val squaredHalfDeltaLatitude = sin(deltaLatitude / 2) * sin(deltaLatitude / 2)
    val squaredHalfDeltaLongitude = sin(deltaLongitude / 2) * sin(deltaLongitude / 2)

    val a = squaredHalfDeltaLatitude +
            cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
            squaredHalfDeltaLongitude

    val centralAngle = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * centralAngle
}

private fun deg2rad(deg: Double): Double {
    return Math.toRadians(deg)
}
