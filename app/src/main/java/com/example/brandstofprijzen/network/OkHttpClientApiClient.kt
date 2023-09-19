package com.example.brandstofprijzen.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resumeWithException

class OkHttpClientApiClient : ApiClient {
    private val client = OkHttpClient()

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun makeApiRequest(url: String): String = suspendCancellableCoroutine { continuation ->
        val request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        continuation.invokeOnCancellation {
            call.cancel()
        }
        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response.body?.string() ?: throw IOException("API request failed"), onCancellation = {})
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(RuntimeException(e))
            }
        })
    }
}