package com.example.zoomtest.network

import com.example.zoomtest.editor.utils.getFileFromCache
import com.example.zoomtest.editor.utils.saveFileFromNetwork
import java.io.File

class NetworkServiceHelper(private val networkInterface: NetworkInterface) {

    suspend fun loadFile(fromCache: Boolean): File {
        if (fromCache){
            return getFileFromCache()
        }
        val responseBody = networkInterface.loadFile("https://file-examples-com.github.io/uploads/2017/10/file-example_PDF_1MB.pdf")
        return saveFileFromNetwork(responseBody)
    }

}