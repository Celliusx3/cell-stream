package com.cellstudio.cellstream.data.source.example.kitsu.models.response

import kotlinx.serialization.Serializable

@Serializable
data class KitsuVideoDataResponse(
    val id: String,
    val attributes: KitsuVideoAttributeResponse,
) {
    @Serializable
    data class KitsuVideoAttributeResponse(
        val synopsis: String,
        val canonicalTitle: String,
        val posterImage: KitsuImageResponse,
        val coverImage: KitsuImageResponse
    )

}