package com.cellstudio.cellstream.data.source.example.kitsu.models.entities

import com.cellstudio.cellstream.data.base.models.entities.Genre
import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuEpisodesResponse
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuVideoResponse

data class KitsuVideoDetails(
    override val id: String,
    override val title: String,
    override val image: String,
    override val episodes: List<EpisodeResponse>?,
    override val genres: List<Genre>?,
    override val synopsis: String?
): VideoDetailsResponse {
    companion object {
        @JvmStatic fun create(response: KitsuVideoResponse, episodes: KitsuEpisodesResponse): KitsuVideoDetails {
            return KitsuVideoDetails(
                response.data.id,
                response.data.attributes.canonicalTitle,
                response.data.attributes.posterImage.original,
                episodes.data.map { KitsuEpisode.create(it) },
                null,
                response.data.attributes.synopsis)
        }
    }
}