package com.example.brandstofprijzen.infrastructure.api.anwb

import android.content.Context
import com.example.brandstofprijzen.domain.Bounds
import com.example.brandstofprijzen.domain.PetrolStationLocation
import com.example.brandstofprijzen.domain.PetrolStation
import com.example.brandstofprijzen.domain.api.FuelData
import com.example.brandstofprijzen.infrastructure.httpclient.ApiClient
import com.example.brandstofprijzen.util.BoundGenerator
import com.example.brandstofprijzen.util.getApiKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder

class AnwbApiService(private val apiClient: ApiClient, context: Context) {


    private var apiKey : String = ""
    private val fuelTypes = mapOf(
        "Diesel (B7)" to "diesel",
        "Euro 95 (E10)" to "euro95",
        "Super Plus 98 (E5)" to "euro98",
        "Premium diesel" to "diesel_special",
        "LPG" to "autogas"
    )

    // on construction of the class get the api key
    init {
        apiKey = getApiKey(context)
    }

    suspend fun getTankstationIds(longitude: Double, latitude: Double, selectedFuel: String): List<String> =
        withContext(Dispatchers.IO) {

            // Generate the bounds for the API request
            val bounds = BoundGenerator().generateBounds(latitude, longitude, distanceToCornersKm = 5.0)

            // Build the URL for the API request
            val url = generateUrlForIds(selectedFuel, bounds)

            // Make the API request using the provided ApiClient instance
            val response = apiClient.makeApiRequest(url)


            // Parse the JSON data into a JSONObject
            val responseJsonObject = JSONObject(response)
            val pointOfInterestsArray = responseJsonObject.getJSONArray("pois")

            // Extract the tank station IDs from the JSON data
            val tankStationIds = List(pointOfInterestsArray.length()) {
                pointOfInterestsArray.getJSONObject(it).getString("id")
            }

            tankStationIds
        }

    private fun generateUrlForIds(selectedFuel: String, bounds: Bounds) : String {
        val fuelType = fuelTypes[selectedFuel] ?: "diesel"
        return "https://api.anwb.nl/v2/pois?apikey=$apiKey&types=fuel%2Cfuel-eu&bounds=${bounds.northEastLat}%2C${bounds.northEastLon}%3A${bounds.southWestLat}%2C${bounds.southWestLon}&details=minimal&fuelTypes=$fuelType"
    }

    private fun generateUrlForFuelData(identifier: String): String {
        return if (identifier.toIntOrNull() != null) {
            "https://api.anwb.nl/v2/pois/fuel/$identifier?apikey=$apiKey"
        } else if (identifier.contains("|")) {
            val formattedIdentifier = URLEncoder.encode(identifier, "utf-8")
            "https://api.anwb.nl/v2/pois/fuel-eu/$formattedIdentifier?apikey=$apiKey"
        } else {
            "https://api.anwb.nl/v2/pois/fuel-eu/$identifier?apikey=$apiKey"
        }
    }

    suspend fun parseFuelData(identifier: String): PetrolStation =
        withContext(Dispatchers.IO) {

            val url = generateUrlForFuelData(identifier)
            val response = apiClient.makeApiRequest(url)

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val adapter = moshi.adapter(FuelData::class.java)
            val fuelData = adapter.fromJson(response)

            if (fuelData == null) {
                PetrolStation(identifier, "", PetrolStationLocation("", "", 0.0, 0.0), emptyMap(), emptyMap())
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

                val locatie = PetrolStationLocation(adres, locality, longitude, latitude)

                PetrolStation(identifier, displayName, locatie, datums, prices)
            }
        }
}