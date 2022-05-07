package com.cellstudio.cellstream.data.source.example.tmdb.models.entities

import com.cellstudio.cellstream.data.base.models.entities.Genre
import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbEpisodesResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbVideoDetailsResponse

data class TmdbVideoDetails(
    override val id: String,
    override val title: String,
    override val image: String,
    override val episodes: List<EpisodeResponse>?,
    override val genres: List<Genre>?,
    override val synopsis: String?
): VideoDetailsResponse {
    companion object {
        const val YOUTUBE = "YouTube"
        @JvmStatic fun create(response: TmdbVideoDetailsResponse, url: String, episodes: TmdbEpisodesResponse): TmdbVideoDetails {
            return TmdbVideoDetails(
                response.id.toString(),
                response.title,
                "${url}${response.poster_path}",
                episodes.results.filter { it.site == YOUTUBE }.map { TmdbEpisode.create(it) },
                null,
                response.overview)
        }
    }
}