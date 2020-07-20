package com.example.zoomtest.editor.model

import android.graphics.Bitmap
import java.util.*

data class PdfPage(val id: UUID = UUID.randomUUID(), val page: Bitmap): Comparable<PdfPage> {

    override fun compareTo(other: PdfPage): Int {
        return other.id.compareTo(id)
    }

}