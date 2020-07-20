package com.example.zoomtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomtest.editor.model.PdfDocument
import com.example.zoomtest.network.buildNetworkInterface
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private val networkServiceHelper by lazy {
        buildNetworkInterface()
    }
    val fileObserver: LiveData<PdfDocument> by lazy {
        MutableLiveData<PdfDocument>()
    }

    fun onCreate(fromCache: Boolean) {
        viewModelScope.launch {
            val file = networkServiceHelper.loadFile(fromCache)
            val document = PdfDocument()
            document.openDocument(file)
            (fileObserver as MutableLiveData<PdfDocument>).postValue(document)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fileObserver.value?.closeDocument()
    }

}