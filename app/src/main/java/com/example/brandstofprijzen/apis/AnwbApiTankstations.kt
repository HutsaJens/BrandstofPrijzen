package com.example.brandstofprijzen.apis

import android.content.Context
import com.example.brandstofprijzen.model.Locatie
import com.example.brandstofprijzen.model.Tankstation
import com.example.brandstofprijzen.model.api.FuelData
import com.example.brandstofprijzen.network.ApiClient
import com.example.brandstofprijzen.util.getApiKey

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


class AnwbApiTankstations(private val apiClient: ApiClient, private val context: Context) {

    // Function to parse the list of fuel data for given identifiers
    suspend fun parseFuelDataList(identifiers: List<String>): List<Tankstation> =
        withContext(Dispatchers.IO) {
            // Create a list of Deferred<Tankstation> to represent the async tasks
            val deferredList = identifiers.map { identifier ->
                async { parseFuelData(identifier) }
            }

            // Wait for all the async tasks to complete and retrieve the results
            deferredList.awaitAll()
        }

    private suspend fun getFuelData(identifier: String): String {
        val apiKey = getApiKey(context)

        val url = createApiUrl(identifier, apiKey)
        return apiClient.makeApiRequest(url)
    }

    private fun createApiUrl(identifier: String, apiKey: String): String {
        val url: String = if (identifier.toIntOrNull() != null) {
            "https://api.anwb.nl/v2/pois/fuel/$identifier"
        } else if (identifier.contains("|")) {
            val formattedIdentifier = changeIdentifier(identifier)
            "https://api.anwb.nl/v2/pois/fuel-eu/$formattedIdentifier"
        } else {
            "https://api.anwb.nl/v2/pois/fuel-eu/$identifier"
        }

        return "$url?apikey=$apiKey"
    }

    // Function to parse fuel data for a single identifier
    suspend fun parseFuelData(identifier: String): Tankstation =
        withContext(Dispatchers.IO) {

            // Make Request
            val response = getFuelData(identifier)

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val adapter = moshi.adapter(FuelData::class.java)
            val fuelData = adapter.fromJson(response)

            if (fuelData == null) {
                Tankstation(identifier, "", Locatie("", "", 0.0, 0.0), emptyMap(), emptyMap())
            } else {
                // Extract relevant properties from fuelData
                val displayName = fuelData.getShowName()
                val locality = fuelData.getLocality()
                val adres = fuelData.getAdres()
                val longitude = fuelData.getLongitude()
                val latitude = fuelData.getLatitude()

                // Extract prices and datums using inline functions
                val prices = fuelData.getPrices()
                val datums = fuelData.getDatums()

                val locatie = Locatie(adres, locality, longitude, latitude)

                Tankstation(identifier, displayName, locatie, datums, prices)
            }
        }

    private fun changeIdentifier(id: String): String {
        return id.replace("|", "%7C")
    }
}

