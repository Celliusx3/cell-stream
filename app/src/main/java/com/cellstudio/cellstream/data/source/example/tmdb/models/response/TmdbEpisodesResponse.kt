package com.cellstudio.cellstream.data.source.example.tmdb.models.response

import kotlinx.serialization.Serializable

@Serializable
data class TmdbEpisodesResponse(
    val results: List<TmdbEpisodeResponse>
) {
    @Serializable
    data class TmdbEpisodeResponse(
        val id: String,
        val name: String,
        val site: String
    )
}