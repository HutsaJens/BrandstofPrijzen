package com.example.brandstofprijzen.application

import android.content.Context
import android.content.SharedPreferences
import com.example.brandstofprijzen.domain.PetrolStation
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PetrolCacheManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("PetrolStationCache", Context.MODE_PRIVATE)

    fun savePetrolStation(petrolStation: PetrolStation, selectedFuel: String) {
        val editor = prefs.edit()
        val key = "${petrolStation.id}_$selectedFuel"
        val serializedPetrolStation = Gson().toJson(petrolStation)
        editor.putString(key, serializedPetrolStation)
        editor.apply()
    }

    fun getCachedPetrolStation(id: String, selectedFuel: String): PetrolStation? {
        val key = "${id}_$selectedFuel"
        val serializedPetrolStation = prefs.getString(key, null) ?: return null
        return Gson().fromJson(serializedPetrolStation, PetrolStation::class.java)
    }

    fun removeOutdatedEntries() {
        val editor = prefs.edit()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val allEntries = prefs.all

        allEntries.forEach { entry ->
            val serializedPetrolStation = entry.value as? String ?: return@forEach
            val petrolStation = Gson().fromJson(serializedPetrolStation, PetrolStation::class.java)

            // Assuming the date format is "dd-MM-yyyy" as used before
            val lastPriceChangeDate = petrolStation.lastPriceChangeDates.values.firstOrNull() ?: return@forEach

            // If the date is older than the current date, remove it from the cache
            if (lastPriceChangeDate != currentDate) {
                editor.remove(entry.key)
            }
        }

        editor.apply()
    }
}
