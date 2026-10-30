package com.naufal.recipeexplorer.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HttpException(val code: Int) : Exception("HTTP $code")

object HttpClient {

    private const val TIMEOUT_MS = 15_000

    suspend fun getText(url: String): String = getBytes(url).decodeToString()

    suspend fun getBytes(url: String): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            val code = connection.responseCode
            if (code !in 200..299) throw HttpException(code)
            connection.inputStream.use { it.readBytes() }
        } finally {
            connection.disconnect()
        }
    }
}
