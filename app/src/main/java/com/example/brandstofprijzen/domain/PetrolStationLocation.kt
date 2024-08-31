package com.example.brandstofprijzen.domain

data class PetrolStationLocation(
    val adres: String,
    val locatie: String,
    val longitude: Double,
    val latitude: Double
) {
    override fun toString(): String {
        return "$adres - $locatie: $locatie"
    }
}