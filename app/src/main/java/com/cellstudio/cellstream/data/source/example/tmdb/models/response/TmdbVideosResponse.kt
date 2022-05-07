package com.cellstudio.cellstream.data.source.example.tmdb.models.response

import kotlinx.serialization.Serializable

@Serializable
data class TmdbVideosResponse(
    val results: List<TmdbVideoResponse>
) {
    @Serializable
    data class TmdbVideoResponse(
        val id: Int,
        val title: String,
        val poster_path: String?
    )
}