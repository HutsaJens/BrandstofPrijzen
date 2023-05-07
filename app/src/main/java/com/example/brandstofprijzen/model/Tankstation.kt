package com.example.brandstofprijzen.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

    fun getFormattedCheckDate(dateFormat: String = "dd-MM-yyyy"): Map<String, String> {
        val formattedCheckDate = mutableMapOf<String, String>()
        for ((key, value) in checkDate) {
            val parsedDate = LocalDate.parse(value)
            formattedCheckDate[key] = parsedDate.format(DateTimeFormatter.ofPattern(dateFormat))
        }
        return formattedCheckDate
    }
}
