package com.example.brandstofprijzen.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.net.URL


private const val url = "https://533.static.anwb.nl/routing-chunk.js"

suspend fun scrapeAPIKey(): String? = withContext(Dispatchers.IO) {
    try {
        val response = URL(url).readText()
        val regexPattern = """prod:\s*"([^"]+)"""".toRegex()
        val matchResult = regexPattern.find(response)
        val apiKey = matchResult?.groupValues?.getOrNull(1)
        return@withContext apiKey
    } catch (e: IOException) {
        e.printStackTrace()
        return@withContext null
    }
}




