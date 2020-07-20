package com.example.zoomtest.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun buildNetworkInterface(): NetworkServiceHelper {
    val networkInterface = buildRetrofit()
            .create(NetworkInterface::class.java)
    return NetworkServiceHelper(networkInterface)
}

private fun buildRetrofit(): Retrofit {
    return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.example.com")
            .build()
}