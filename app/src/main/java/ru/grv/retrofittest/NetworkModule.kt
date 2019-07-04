package ru.grv.retrofittest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


private const val URL = "https://storage.yandexcloud.net"

private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)

val retrofit: Retrofit = Retrofit.Builder()
        .client(httpClient.build())
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()





