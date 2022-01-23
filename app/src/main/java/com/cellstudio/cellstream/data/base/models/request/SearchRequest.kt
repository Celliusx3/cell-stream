package com.cellstudio.cellstream.data.base.models.request

data class SearchRequest (
    val query: String,
    val page: Int?= null
)