package com.example.brandstofprijzen.domain.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class FuelData(
    @Json(name = "displayName") val displayName: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "address") val address: AddressData?,
    @Json(name = "geo") val geo: GeoData?,
    @Json(name = "fuels") val fuels: List<FuelItemData>?,
    @Json(name = "lastImportedTimestamp") val lastImportedTimestamp: String?
) {
    fun getShowName(): String {
        return if (!displayName.isNullOrBlank()) {
            displayName
        } else {
            name ?: ""
        }
    }

    fun getLocality(): String {
        return address?.addressLocality ?: ""
    }

    fun getAdres(): String {
        return address?.streetAddress ?: ""
    }

    fun getLongitude(): Double {
        return geo?.longitude ?: 0.0
    }

    fun getLatitude(): Double {
        return geo?.latitude ?: 0.0
    }

    fun getPrices(): Map<String, String> {
        // Use the `associate` function to transform the list of fuels into a map.
        // Each fuel item is mapped to a key-value pair with the fuel type as the key and the formatted price as the value.
        // If fuels is null, return an empty map.
        return fuels?.associate { fuelItem ->
            val fuelType = fuelItem.name ?: ""
            val priceValue = fuelItem.price?.value ?: return emptyMap()

            if(priceValue == "0") {
                return emptyMap()
            }

            // Format the price value as Euro currency if it is not "null" and has at least two characters.
            // Otherwise, return the original value as is.
            val formattedValue = if (priceValue.length == 3) {
                "€0.$priceValue"
            } else if (priceValue != "null") {
                "€${priceValue.substring(0, 1)}.${priceValue.substring(1)}"
            } else {
                "€$priceValue"
            }

            // Map the fuelType to its corresponding formattedValue.
            fuelType to formattedValue
        } ?: emptyMap() // If fuels is null, return an empty map.
    }

    fun getDatums(): Map<String, String> {
        // Use the `associate` function to transform the list of fuels into a map.
        // Each fuel item is mapped to a key-value pair with the fuel type as the key and the formatted date as the value.
        // If fuels is null, return an empty map.
        return fuels?.associate { fuelItem ->
            val fuelType = fuelItem.name ?: ""
            val recordDate = lastImportedTimestamp ?: ""

            // If the record date is not blank, parse it to a LocalDate object and format it as "dd-MM-yyyy".
            // Otherwise, use "Onbekend" as the value for the fuel type in the map.
            if (recordDate.isNotBlank()) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                val date = LocalDate.parse(recordDate, formatter)
                fuelType to date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            } else {
                fuelType to "Onbekend"
            }
        } ?: emptyMap() // If fuels is null, return an empty map.
    }

}