package com.example.zoomtest.editor.pdf_core.cache

interface PdfMemoryCache<T, R> {

    fun storeItem(key: R, item: T)

    fun getItem(key: R): T?

}