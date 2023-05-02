package com.example.brandstofprijzen.model

data class Brandstof(
    val naam: String,
    val locatie: String,
    val adres: String,
    val prijs: String,
    val checkDate: String
) {
    override fun toString(): String {
        return "$naam - $locatie: $prijs"
    }
}
