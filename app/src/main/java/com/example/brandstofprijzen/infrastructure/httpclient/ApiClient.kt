package com.example.brandstofprijzen.infrastructure.httpclient

interface ApiClient {
    suspend fun makeApiRequest(url: String): String
}