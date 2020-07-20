package com.example.zoomtest.editor.model

import android.graphics.Bitmap
import android.net.Uri
import com.example.zoomtest.editor.pdf_core.PdfCore
import com.example.zoomtest.editor.pdf_core.cache.PdfMemoryCache
import com.example.zoomtest.editor.pdf_core.cache.PdfMemoryCacheImpl
import java.io.File

class PdfDocument {

    private val core: PdfCore by lazy {
        PdfCore.getInstance()
    }
    private val cache: PdfMemoryCache<Bitmap, Int> by lazy {
        PdfMemoryCacheImpl()
    }

    fun openDocument(file: File) {
        core.openRender(file)
    }

    fun openDocument(uri: Uri) {
        core.openRender(uri)
    }

    fun getPagesCount(): Int {
        return core.getPageCount()
    }

    fun getPages(start: Int = 0, end: Int): List<PdfPage> {
        var newEnd = start + end
        if (newEnd > getPagesCount()) {
            newEnd = getPagesCount()
        }
        val list = mutableListOf<PdfPage>()

        for (it in start until newEnd) {
            val page: Bitmap? = cache.getItem(it)
            list.add(PdfPage(page = page
                    ?: core.renderPage(it)))
        }
        return list
    }

    fun getPage(position: Int): PdfPage {
        return PdfPage(page = cache.getItem(position)
                ?: core.renderPage(position))
    }

    fun closeDocument() {
        core.closeRenderer()
    }

}