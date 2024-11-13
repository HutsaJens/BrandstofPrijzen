package com.example.brandstofprijzen.domain.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoData(
    @Json(name = "longitude") var longitude: Double?,
    @Json(name = "latitude") var latitude: Double?
)