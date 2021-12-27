package com.cellstudio.cellstream.data.services.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class DefaultNetworkService: NetworkService {
    private var _client: OkHttpClient = createOkHttpClient()
    override val client: OkHttpClient = _client

    private fun createOkHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return createOkHttpClientBuilder()
            .addInterceptor(logging)
            .build()
    }
}