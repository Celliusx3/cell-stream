package com.cellstudio.cellstream.data.source.example.tmdb.models.entities

import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbEpisodesResponse

data class TmdbEpisode(
    override val id: String,
    override val title: String) : EpisodeResponse {
    companion object {
        @JvmStatic fun create(response: TmdbEpisodesResponse.TmdbEpisodeResponse): TmdbEpisode {
            return TmdbEpisode(
                response.id,
                response.name
            )
        }
    }
}