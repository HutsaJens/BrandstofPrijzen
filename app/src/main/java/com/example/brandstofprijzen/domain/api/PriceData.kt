package com.example.brandstofprijzen.domain.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PriceData(
    @Json(name = "value") val value: String?
)