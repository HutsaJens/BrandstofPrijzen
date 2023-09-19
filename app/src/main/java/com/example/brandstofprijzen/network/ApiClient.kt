package com.example.brandstofprijzen.network

interface ApiClient {
    suspend fun makeApiRequest(url: String): String
}