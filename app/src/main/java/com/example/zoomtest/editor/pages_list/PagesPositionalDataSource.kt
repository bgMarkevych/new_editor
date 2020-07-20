package com.example.zoomtest.editor.pages_list

import androidx.paging.PositionalDataSource
import com.example.zoomtest.editor.model.PdfDocument
import com.example.zoomtest.editor.model.PdfPage

class PagesPositionalDataSource(private val document: PdfDocument): PositionalDataSource<PdfPage>(){

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<PdfPage>) {
        callback.onResult(document.getPages(params.startPosition, params.loadSize))
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<PdfPage>) {
        callback.onResult(document.getPages(end = params.requestedLoadSize), params.requestedStartPosition, document.getPagesCount())
    }

}