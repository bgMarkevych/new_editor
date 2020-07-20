package com.example.zoomtest.editor.pdf_core

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.zoomtest.App
import com.example.zoomtest.editor.utils.getPdfFileFromUri
import com.example.zoomtest.editor.pdf_core.exception.CoreInitializeException
import com.example.zoomtest.editor.pdf_core.exception.CoreNotInitializedException
import java.io.File
import java.lang.Exception

class PdfCore private constructor() {

    companion object {

        private var core: PdfCore? = null

        fun getInstance(): PdfCore {
            if (core == null) {
                core = PdfCore()
            }
            return core!!
        }

    }

    private lateinit var pdfRenderer: PdfRenderer

    @Throws(CoreInitializeException::class)
    fun openRender(file: File) {
        try {
            val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor)
        } catch (e: Exception) {
            e.printStackTrace()
            throw CoreInitializeException()
        }
    }

    fun openRender(uri: Uri) {
        openRender(getPdfFileFromUri(App.getInstance().applicationContext, uri))
    }

    fun renderPage(page: Int): Bitmap {
        if (!::pdfRenderer.isInitialized) {
            throw CoreNotInitializedException()
        } else {
            val pdfPage = pdfRenderer.openPage(page)
            val bitmap = Bitmap.createBitmap(pdfPage.width * 2, pdfPage.height * 2, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            pdfPage.close()
            return bitmap
        }
    }

    fun getPageCount(): Int {
        if (!::pdfRenderer.isInitialized) {
            throw CoreNotInitializedException()
        } else {
            return pdfRenderer.pageCount
        }
    }

    fun closeRenderer() {
        if (!::pdfRenderer.isInitialized) {
            return
        }
        pdfRenderer.close()
    }

}