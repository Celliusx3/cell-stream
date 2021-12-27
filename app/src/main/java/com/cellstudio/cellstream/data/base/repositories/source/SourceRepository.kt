package com.cellstudio.cellstream.data.base.repositories.source

import com.cellstudio.cellstream.data.base.models.entities.Filter
import com.cellstudio.cellstream.data.base.models.request.VideoListRequest
import com.cellstudio.cellstream.data.base.models.response.EpisodeDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoResponse

interface SourceRepository {
    val name: String
    val filters: List<Filter>

    fun getHome(request: VideoListRequest): List<VideoResponse>
    fun getVideoDetails(id: String): VideoDetailsResponse
    fun getEpisodeDetails(id: String): EpisodeDetailsResponse
}