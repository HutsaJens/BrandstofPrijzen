package com.example.brandstofprijzen.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressData(
    @Json(name = "addressLocality") val addressLocality: String?,
    @Json(name = "streetAddress") val streetAddress: String?
)