package com.naufal.recipeexplorer.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageLoader {

    private val cache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    fun cached(url: String, maxSizePx: Int): Bitmap? = cache.get(cacheKey(url, maxSizePx))

    suspend fun load(url: String, maxSizePx: Int): Bitmap {
        cached(url, maxSizePx)?.let { return it }
        val bytes = HttpClient.getBytes(url)
        val bitmap = withContext(Dispatchers.Default) { decode(bytes, maxSizePx) }
            ?: throw IllegalArgumentException("Not an image: $url")
        cache.put(cacheKey(url, maxSizePx), bitmap)
        return bitmap
    }

    private fun decode(bytes: ByteArray, maxSizePx: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)

        var sampleSize = 1
        while (bounds.outWidth / (sampleSize * 2) >= maxSizePx &&
            bounds.outHeight / (sampleSize * 2) >= maxSizePx
        ) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    private fun cacheKey(url: String, maxSizePx: Int) = "$url@$maxSizePx"
}
