package com.example.zoomtest.editor.utils

import android.content.Context
import android.net.Uri
import com.example.zoomtest.App
import okhttp3.ResponseBody
import java.io.*

const val FILE_NAME = "file_to_show.pdf"

fun getAppRootFolder(context: Context): File {
    val folder = File(context.filesDir, "pdf_folder")
    if (!folder.exists()) {
        folder.mkdir()
    }
    return folder
}

fun getPdfFileFromUri(context: Context, uri: Uri, rewrite: Boolean = false): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val rooFolder = getAppRootFolder(context)
    var fileName = uri.lastPathSegment!!
    if (!fileName.endsWith(".pdf")) {
        fileName += ".pdf"
    }
    val file = File(rooFolder, fileName)
    if (file.exists() && rewrite) {
        return file
    }
    if (!file.exists()) {
        file.createNewFile()
    } else {
        file.delete()
        file.createNewFile()
    }
    val outputStream = FileOutputStream(file)
    var read: Int
    val bytes = ByteArray(1024)
    do {
        read = inputStream!!.read(bytes)
        outputStream.write(bytes, 0, read)
    } while (read > 0)
    outputStream.close()
    inputStream.close()
    return file
}

fun saveFileFromNetwork(responseBody: ResponseBody): File {
    val file = File(getAppRootFolder(App.getInstance().applicationContext), FILE_NAME)
//    if (file.exists()) {
//        file.delete()
//    }
//    file.createNewFile()
//    val inputStream = responseBody.byteStream()
//    val outputStream = FileOutputStream(file)
//    val bytes = ByteArray(inputStream.available())
//
//    while (true) {
//        var read = 0
//        read = inputStream.read(bytes)
//        if (read != -1){
//            break
//        }
//        outputStream.write(bytes, 0, read)
//    }
//    outputStream.close()
//    inputStream.close()
//    return file

    val bis = BufferedInputStream(responseBody.byteStream())
    val bos: BufferedOutputStream
    val fos = FileOutputStream(file, false)
    bos = BufferedOutputStream(fos)
    var inByte: Int
    while (bis.read().also { inByte = it } != -1) {
        bos.write(inByte)
    }
    bis.close()
    bos.flush()
    bos.close()
    fos.flush()
    fos.close()
    return file

}

fun getFileFromCache(): File {
    return File(getAppRootFolder(App.getInstance().applicationContext), FILE_NAME)
}