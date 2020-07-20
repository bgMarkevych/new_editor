package com.example.zoomtest.editor.pages_list

import androidx.paging.PagedList
import com.example.zoomtest.editor.model.PdfDocument
import com.example.zoomtest.editor.utils.getMainThreadExecutor
import com.example.zoomtest.editor.utils.getSingleThreadExecutor
import java.util.concurrent.Executor

class PagesListBuilder {

    private lateinit var executor: Executor
    private lateinit var notifyExecutor: Executor
    private lateinit var document: PdfDocument
    private var initialKey = 0
    private var enablePlaceHolders = false
    private var pagesSize = 10
    private var initialPagesSize = 20
    private var prefetchDistance = 5

    fun setBackgroundThreadExecutor(executor: Executor): PagesListBuilder {
        this.executor = executor
        return this
    }

    fun setNotifyThreadExecutor(executor: Executor): PagesListBuilder {
        this.notifyExecutor = executor
        return this
    }

    fun setInitialKey(key: Int): PagesListBuilder {
        this.initialKey = key
        return this
    }

    fun setPdfDocument(document: PdfDocument): PagesListBuilder {
        this.document = document
        return this
    }

    fun setEnablePlaceholders(boolean: Boolean): PagesListBuilder {
        this.enablePlaceHolders = boolean
        return this
    }

    fun setPageSize(size: Int): PagesListBuilder {
        this.pagesSize = size
        return this
    }

    fun setInitialLoadSizeHint(size: Int): PagesListBuilder {
        this.initialPagesSize = size
        return this
    }

    fun setPrefetchDistance(distance: Int): PagesListBuilder {
        this.prefetchDistance = distance
        return this
    }

    fun build(): PagesAdapter {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(enablePlaceHolders)
                .setPageSize(pagesSize)
                .setInitialLoadSizeHint(initialPagesSize)
                .setPrefetchDistance(prefetchDistance)
                .build()

        val dataSource = PagesPositionalDataSource(document)


        if (!this::executor.isInitialized) {
            executor = getSingleThreadExecutor()
        }
        if (!this::notifyExecutor.isInitialized) {
            notifyExecutor = getMainThreadExecutor()
        }
        val pagedList = PagedList.Builder(dataSource, config)
                .setFetchExecutor(executor)
                .setInitialKey(initialKey)
                .setNotifyExecutor(notifyExecutor)
                .build()

        val adapter = PagesAdapter()
        adapter.submitList(pagedList)
        return adapter
    }

}