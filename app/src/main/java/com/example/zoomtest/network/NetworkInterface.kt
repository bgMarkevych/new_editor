package com.example.zoomtest.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface NetworkInterface {

    @GET
    suspend fun loadFile(@Url url: String): ResponseBody

}