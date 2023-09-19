package com.example.brandstofprijzen.model

data class Locatie(
    val adres: String,
    val locatie: String,
    val longitude: Double,
    val latitude: Double
) {
    override fun toString(): String {
        return "$adres - $locatie: $locatie"
    }
}