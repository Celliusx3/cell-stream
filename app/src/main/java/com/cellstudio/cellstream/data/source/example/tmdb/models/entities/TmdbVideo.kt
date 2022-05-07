package com.cellstudio.cellstream.data.source.example.tmdb.models.entities

import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbVideosResponse

data class TmdbVideo(
    override val id: String,
    override val title: String,
    override val image: String,
): VideoResponse {
    companion object {
        @JvmStatic fun create(response: TmdbVideosResponse.TmdbVideoResponse, url: String): TmdbVideo {
            return TmdbVideo(response.id.toString(),
                response.title,
                "${url}${response.poster_path}")
        }
    }
}