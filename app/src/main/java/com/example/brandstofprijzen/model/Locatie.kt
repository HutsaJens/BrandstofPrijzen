package com.example.brandstofprijzen.model

data class Locatie(
    val adres: String,
    val locatie: String,
    var longitude: Double = 0.0,
    var latitude: Double = 0.0
) {
    override fun toString(): String {
        return "$adres - $locatie: $locatie"
    }
}