package com.example.brandstofprijzen.util

import com.example.brandstofprijzen.model.Bounds
import kotlin.math.cos

class BoundGenerator() {

    fun generateBounds(latitude: Double, longitude: Double, distanceToCornersKm: Double): Bounds {

        val degreePerKm = 1.0 / 111.0

        val deltaLat = distanceToCornersKm * degreePerKm
        val deltaLon = distanceToCornersKm * degreePerKm / cos(Math.toRadians(latitude))

        val northEastLat = latitude + deltaLat
        val northEastLon = longitude + deltaLon
        val southWestLat = latitude - deltaLat
        val southWestLon = longitude - deltaLon

        return Bounds(northEastLat, northEastLon, southWestLat, southWestLon)

    }

}