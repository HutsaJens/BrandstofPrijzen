package com.example.brandstofprijzen.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FuelItemData(
    @Json(name = "name") val name: String?,
    @Json(name = "recordDate") val recordDate: String?,
    @Json(name = "price") val price: PriceData?
)