package com.cellstudio.cellstream.data.base.repositories.source

import android.net.Uri
import com.cellstudio.cellstream.data.services.network.NetworkService
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

abstract class BaseSourceRepository(private val networkService: NetworkService): SourceRepository {
    protected fun getResponse(url: String,
                              params: Map<String, String> = mapOf(),
                              headers: Map<String, String> = mapOf(),): String {
        val apiRequest = getRequest(getUrl(url, params), headers)
        val response = getClient().newCall(apiRequest).execute()
        return response.body?.string()?:""
    }

    protected fun postResponse(url: String,
                               params: Map<String, String> = mapOf(),
                               headers: Map<String, String> = mapOf(),
                               body: RequestBody ?= null): String {
        val apiRequest = postRequest(getUrl(url, params), body, headers)
        val response = getClient().newCall(apiRequest).execute()
        return response.body?.string()?:""
    }

    private fun getRequestBuilder(url: String, headers: Map<String, String> = mapOf()): Request.Builder{
        val all = mapOf("user-agent" to USER_AGENT) + headers
        return Request.Builder()
            .url(url)
            .headers(all.toHeaders())
    }


    private fun postRequest(url: String, body: RequestBody?, headers: Map<String, String>  = mapOf()): Request {
        val builder =  getRequestBuilder(url, headers)
        body?.let {
            builder.post(it)
        }

        return builder.build()
    }

    private fun getRequest(url: String, headers: Map<String, String>  = mapOf()): Request {
        return getRequestBuilder(url, headers).build()
    }

    private fun getUrl(url: String, params: Map<String, String>): String {
        val builder = Uri.parse(url).buildUpon()
        params.forEach {
            builder.appendQueryParameter(it.key, it.value)
        }

        return builder.build().toString()
    }

    private fun getClient(): OkHttpClient {
        return networkService.client
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }
}