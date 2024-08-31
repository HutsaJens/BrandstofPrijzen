package com.example.brandstofprijzen.util

import android.content.Context
import java.io.IOException
import java.util.*

private const val CONFIG = "config.properties"
private const val APIKEY = "api.key"


fun getApiKey(context: Context): String {
    val properties = Properties()
    try {
        val inputStream = context.assets.open(CONFIG)
        properties.load(inputStream)
        return properties.getProperty(APIKEY)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return "" // Return a default value or handle the error case
}

fun updateApiKey(context: Context, value: String) {
    val properties = Properties()
    try {
        val fileInputStream = context.openFileInput(CONFIG)
        properties.load(fileInputStream)
        fileInputStream.close()

        // Update the value of the key
        properties.setProperty(APIKEY, value)

        val fileOutputStream = context.openFileOutput(CONFIG, Context.MODE_PRIVATE)
        properties.store(fileOutputStream, null)
        fileOutputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
    }
}
