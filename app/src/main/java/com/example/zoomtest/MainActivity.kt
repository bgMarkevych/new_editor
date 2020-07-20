package com.example.zoomtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.zoomtest.editor.model.PdfDocument
import com.example.zoomtest.editor.pages_list.PagesAdapter
import com.example.zoomtest.editor.pages_list.PagesListBuilder
import com.example.zoomtest.editor.utils.getMainThreadExecutor
import com.example.zoomtest.editor.utils.getSingleThreadExecutor
import kotlinx.android.synthetic.main.activity_main.*

const val CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: PagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(App.getInstance())
                .create(MainActivityViewModel::class.java)

        viewModel.onCreate(savedInstanceState != null)

        val currentPage = savedInstanceState?.getInt(CURRENT_PAGE_KEY, 0) ?: 0

        viewModel.fileObserver.observe(this, Observer<PdfDocument> {
            adapter = PagesListBuilder()
                    .setBackgroundThreadExecutor(getSingleThreadExecutor())
                    .setNotifyThreadExecutor(getMainThreadExecutor())
                    .setEnablePlaceholders(true)
                    .setPdfDocument(it)
                    .build()
            list.adapter = adapter
            list.currentItem = currentPage
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_PAGE_KEY, list.currentItem)
    }

}