package com.example.brandstofprijzen.location

import org.json.JSONObject
import java.lang.Exception
import java.net.URL

const val username : String = "bootlegbrogle"

fun getCountryCodeFromCoordinates(latitude: Double, longitude: Double): Boolean {
    val url = "http://api.geonames.org/countryCodeJSON?lat=$latitude&lng=$longitude&username=$username"

    // TODO currently does network stuff on ui thread. doesnt work, will crash

    try {
        val response = URL(url).readText()
        val jsonResponse = JSONObject(response)
        val countryCode = jsonResponse.getString("countryCode")

        if (countryCode != "NL") {
            return false
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return true
}
