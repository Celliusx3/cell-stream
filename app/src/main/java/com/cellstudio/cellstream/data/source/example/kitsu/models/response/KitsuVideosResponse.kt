package com.cellstudio.cellstream.data.source.example.kitsu.models.response

import kotlinx.serialization.Serializable

@Serializable
data class KitsuVideosResponse(
    val data: List<KitsuVideoDataResponse>
)