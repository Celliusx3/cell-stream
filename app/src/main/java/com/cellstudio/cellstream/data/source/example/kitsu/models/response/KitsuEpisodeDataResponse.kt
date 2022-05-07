package com.cellstudio.cellstream.data.source.example.kitsu.models.response

import kotlinx.serialization.Serializable

@Serializable
data class KitsuEpisodeDataResponse(
    val id: String,
    val attributes: KitsuEpisodeAttributeResponse,
) {
    @Serializable
    data class KitsuEpisodeAttributeResponse(
        val canonicalTitle: String,
        val thumbnail: KitsuImageResponse,
    )

}