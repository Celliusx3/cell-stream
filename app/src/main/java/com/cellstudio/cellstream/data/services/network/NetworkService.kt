package com.cellstudio.cellstream.data.services.network

import okhttp3.OkHttpClient

interface NetworkService {
    val client: OkHttpClient
}