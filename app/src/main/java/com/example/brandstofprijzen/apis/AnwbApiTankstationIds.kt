package com.example.brandstofprijzen.apis

import android.content.Context
import com.example.brandstofprijzen.jsonparser.JsonParser
import com.example.brandstofprijzen.model.Bounds
import com.example.brandstofprijzen.network.ApiClient
import com.example.brandstofprijzen.util.BoundGenerator
import com.example.brandstofprijzen.util.getApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AnwbApiTankstationIds(private val apiClient: ApiClient) {

    private val fuelTypes = mapOf(
        "Diesel (B7)" to "diesel",
        "Euro 95 (E10)" to "euro95",
        "Super Plus 98 (E5)" to "euro98",
        "Premium diesel" to "diesel_special",
        "LPG" to "autogas"
    )

    // Gets a list of tank station IDs from the ANWB API
    suspend fun getTankstationIds(context: Context, longitude: Double, latitude: Double, selectedFuel: String): List<String> =
        withContext(Dispatchers.IO) {

            // Generate the bounds for the API request
            val bounds = BoundGenerator().generateBounds(latitude, longitude, distanceToCornersKm = 5.0)
            println("getTankstationIds | Bounds: $bounds")



            // Build the URL for the API request
            val url = generateUrl(context, selectedFuel, bounds)
            println("URL: $url")

            // Make the API request using the provided ApiClient instance
            val response = apiClient.makeApiRequest(url)

            // Parse the tank station IDs from the response
            val tankStationIds = JsonParser().parseTankstationIds(response)
            println("Parsed tankstation IDs: $tankStationIds")

            tankStationIds
        }

    private fun generateUrl(context: Context, selectedFuel: String, bounds: Bounds) : String {

//        val currContext = requireContext()

        // Get the API key
        val apiKey = getApiKey(context)

        val fuelType = fuelTypes[selectedFuel] ?: "diesel"
        return "https://api.anwb.nl/v2/pois?apikey=$apiKey&types=fuel%2Cfuel-eu&bounds=${bounds.northEastLat}%2C${bounds.northEastLon}%3A${bounds.southWestLat}%2C${bounds.southWestLon}&details=minimal&fuelTypes=$fuelType"
    }

}
