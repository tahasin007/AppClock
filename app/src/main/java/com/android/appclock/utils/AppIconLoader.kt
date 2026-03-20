package com.android.appclock.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap

class AppIconLoader(
    private val context: Context
) {
    private val packageManager = context.packageManager
    private val memoryCache = object : LruCache<String, Bitmap>(maxCacheSizeInKb()) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }

    suspend fun loadIcon(packageName: String, sizePx: Int): Bitmap? {
        if (packageName.isBlank() || sizePx <= 0) {
            return null
        }

        val cacheKey = "$packageName#$sizePx"
        memoryCache.get(cacheKey)?.let { return it }

        return runCatching {
            packageManager.getApplicationIcon(packageName)
                .toBitmap(width = sizePx, height = sizePx)
        }.getOrNull()?.also { bitmap ->
            memoryCache.put(cacheKey, bitmap)
        }
    }

    fun clear() {
        memoryCache.evictAll()
    }

    private fun maxCacheSizeInKb(): Int {
        return (Runtime.getRuntime().maxMemory() / 1024L / 16L).toInt()
    }
}

