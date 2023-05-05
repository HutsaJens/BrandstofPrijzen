package com.example.brandstofprijzen.model

data class Tankstation(
    val id: Int,
    val naam: String,
    val locatie: Locatie,
    val checkDate: Map<String, String>,
    val prijs: Map<String, String>
) {
    override fun toString(): String {
        return "$naam - ${locatie.locatie}: $prijs"
    }
}
