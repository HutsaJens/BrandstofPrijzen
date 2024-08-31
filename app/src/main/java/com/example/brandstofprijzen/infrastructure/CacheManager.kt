package com.example.brandstofprijzen.infrastructure

import android.content.Context
import okio.IOException
import okio.buffer
import okio.source
import java.io.File

class CacheManager(private val context: Context) {

    private fun getCacheFile(): File {
        // Get the cache directory
        val cacheDir = context.cacheDir
        val fileName = "keys.txt"

        // Create a new file inside the cache directory
        return File(cacheDir, fileName)
    }

    fun writeToCacheFile(content: String): Boolean {
        val cacheFile = getCacheFile()

        return try {
            cacheFile.appendText(content)
            cacheFile.appendText("\n") // Add a newline character after each content
            true
        } catch (e: IOException) {
            println(e.message)
            false
        }
    }

    fun readFromCacheFile(): String {
        val cacheFile = getCacheFile()

        return try {
            cacheFile.source().buffer().use { buffer ->
                buffer.readUtf8()
            }
        } catch (e: IOException) {
            println(e.message)
            ""
        }
    }

    fun removeFromCacheFile(contentToRemove: String): Boolean {
        val cacheFile = getCacheFile()

        return try {
            val updatedContent = readFromCacheFile().replace(contentToRemove + "\n", "")
            cacheFile.writeText(updatedContent)
            true
        } catch (e: IOException) {
            println(e.message)
            false
        }
    }

    fun deleteCacheFile(cacheFile: File): Boolean {
        return cacheFile.delete()
    }
}
