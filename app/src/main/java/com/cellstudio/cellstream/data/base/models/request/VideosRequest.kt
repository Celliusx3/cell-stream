package com.cellstudio.cellstream.data.base.models.request

data class VideosRequest (
    val sortBy: String?= null,
    val genre: String?= null,
    val page: Int?= null
)