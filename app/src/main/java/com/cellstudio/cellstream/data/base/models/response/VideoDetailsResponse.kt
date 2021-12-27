package com.cellstudio.cellstream.data.base.models.response

import com.cellstudio.cellstream.data.base.models.entities.Genre

interface VideoDetailsResponse {
    val id: String
    val title: String
    val image: String
    val episodes: List<EpisodeResponse>?
    val genres: List<Genre>?
    val synopsis: String?
}