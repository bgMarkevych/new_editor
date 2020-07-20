package com.example.zoomtest.editor.pdf_core.cache

import android.graphics.Bitmap
import androidx.collection.LruCache

class PdfMemoryCacheImpl : PdfMemoryCache<Bitmap, Int> {

    private val cache by lazy {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        object : LruCache<Int, Bitmap>(cacheSize) {
            override fun sizeOf(key: Int, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    override fun storeItem(key: Int, item: Bitmap) {
        cache.put(key, item)
    }

    override fun getItem(key: Int): Bitmap? {
        return cache[key]
    }
}