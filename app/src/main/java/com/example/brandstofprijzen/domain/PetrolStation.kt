package com.example.brandstofprijzen.domain

data class PetrolStation(
    val id: String,
    val name: String,
    val location: PetrolStationLocation,
    val lastPriceChangeDates: Map<String, String>,
    val prices: Map<String, String>
) {
    override fun toString(): String {
        return "$name - ${location.locatie}: ${prices.values.firstOrNull() ?: ""}"
    }
    fun toString(selectedFuel: String): String {
        return "$name - ${location.locatie}: ${prices[selectedFuel] ?: ""}"
    }
}
