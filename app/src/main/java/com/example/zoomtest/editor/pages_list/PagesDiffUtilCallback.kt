package com.example.zoomtest.editor.pages_list

import androidx.recyclerview.widget.DiffUtil
import com.example.zoomtest.editor.model.PdfPage

class PagesDiffUtilCallback : DiffUtil.ItemCallback<PdfPage>() {

    override fun areItemsTheSame(oldItem: PdfPage, newItem: PdfPage): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PdfPage, newItem: PdfPage): Boolean {
        return oldItem.id == newItem.id
    }

}