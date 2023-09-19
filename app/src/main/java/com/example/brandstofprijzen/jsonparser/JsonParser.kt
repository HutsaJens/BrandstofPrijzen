package com.example.brandstofprijzen.jsonparser

import org.json.JSONObject

class JsonParser {

    // Parses tank station IDs from JSON data returned by the ANWB API
    fun parseTankstationIds(jsonData: String): List<String> {

        val tankStationIds = mutableListOf<String>()

        // Parse the JSON data into a JSONObject
        val jsonObject = JSONObject(jsonData)
        val poisArray = jsonObject.getJSONArray("pois")

        // Loop through each element in the "pois" array
        for (i in 0 until poisArray.length()) {
            // Get the current POI as a JSONObject
            val poi = poisArray.getJSONObject(i)
            val id = poi.getString("id")
            tankStationIds.add(id)
        }

        return tankStationIds
    }
}