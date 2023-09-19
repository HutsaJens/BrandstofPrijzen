package com.example.brandstofprijzen.model

// Data class representing the bounds of a geographical area
data class Bounds(
    val southWestLat: Double,
    val southWestLon: Double,
    val northEastLat: Double,
    val northEastLon: Double
)
