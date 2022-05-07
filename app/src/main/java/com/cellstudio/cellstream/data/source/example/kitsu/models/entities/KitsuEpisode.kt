package com.cellstudio.cellstream.data.source.example.kitsu.models.entities

import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuEpisodeDataResponse


data class KitsuEpisode(
    override val id: String,
    override val title: String) : EpisodeResponse {
    companion object {
        @JvmStatic fun create(response: KitsuEpisodeDataResponse): KitsuEpisode {
            return KitsuEpisode(
                response.id,
                response.attributes.canonicalTitle
            )
        }
    }
}