package com.cellstudio.cellstream.data.base.repositories.source

import com.cellstudio.cellstream.data.services.network.NetworkService
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request

abstract class BaseSourceRepository(private val networkService: NetworkService): SourceRepository {
    protected abstract val url: String

    protected fun getRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .headers(mapOf("user-agent" to USER_AGENT).toHeaders())
            .build()
    }

    protected open fun getClient(): OkHttpClient {
        return networkService.client
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }
}