package com.cellstudio.cellstream.data.source.example.kitsu.models.entities

import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuVideoDataResponse

data class KitsuVideo(
    override val id: String,
    override val title: String,
    override val image: String,
): VideoResponse {
    companion object {
        @JvmStatic fun create(response: KitsuVideoDataResponse): KitsuVideo {
            return KitsuVideo(
                response.id,
                response.attributes.canonicalTitle,
                response.attributes.posterImage.original)
        }
    }
}