package com.example.zoomtest.editor.pages_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zoomtest.R
import com.example.zoomtest.editor.model.PdfPage
import com.example.zoomtest.editor.widget.EditorPageUIBuilder

class PagesAdapter : PagedListAdapter<PdfPage, PagesAdapter.PageViewHolder>(PagesDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.page_layout, parent, false))
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(page: PdfPage) {
            EditorPageUIBuilder(itemView.context)
                    .setContainer(itemView.findViewById(R.id.container))
                    .setPage(page)
                    .build()
        }

    }

}