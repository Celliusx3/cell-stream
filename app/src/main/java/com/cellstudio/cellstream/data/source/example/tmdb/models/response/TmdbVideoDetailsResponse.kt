package com.cellstudio.cellstream.data.source.example.tmdb.models.response

import kotlinx.serialization.Serializable

@Serializable
data class TmdbVideoDetailsResponse(
    val overview: String,
    val id: Int,
    val title: String,
    val poster_path: String
)