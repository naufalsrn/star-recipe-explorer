package com.naufal.recipeexplorer.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Downloads and caches images, replacing a library such as Coil or Glide.
 *
 * - Images are scaled down while decoding so a small thumbnail does not hold
 *   a full-size bitmap in memory.
 * - Decoded bitmaps are kept in an in-memory LRU ("least recently used")
 *   cache, so scrolling back up does not download images again.
 */
object ImageLoader {

    // Use up to 1/8 of the app's available memory for cached bitmaps.
    private val cache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    fun cached(url: String, maxSizePx: Int): Bitmap? = cache.get(cacheKey(url, maxSizePx))

    /** Returns the image at [url], scaled so its sides are not much larger than [maxSizePx]. */
    suspend fun load(url: String, maxSizePx: Int): Bitmap {
        cached(url, maxSizePx)?.let { return it }
        val bytes = HttpClient.getBytes(url)
        val bitmap = withContext(Dispatchers.Default) { decode(bytes, maxSizePx) }
            ?: throw IllegalArgumentException("Not an image: $url")
        cache.put(cacheKey(url, maxSizePx), bitmap)
        return bitmap
    }

    private fun decode(bytes: ByteArray, maxSizePx: Int): Bitmap? {
        // First read only the image's dimensions, then pick a scale factor.
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
