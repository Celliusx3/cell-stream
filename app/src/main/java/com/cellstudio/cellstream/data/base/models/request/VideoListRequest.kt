package com.cellstudio.cellstream.data.base.models.request

data class VideoListRequest (
    val sortBy: String?= null,
    val genre: String?= null,
    val page: Int?= null
)