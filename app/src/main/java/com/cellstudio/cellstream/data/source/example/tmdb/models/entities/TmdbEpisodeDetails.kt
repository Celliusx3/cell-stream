package com.cellstudio.cellstream.data.source.example.tmdb.models.entities

import com.cellstudio.cellstream.data.base.models.response.EpisodeDetailsResponse

data class TmdbEpisodeDetails(
    override val url: String,
    override val extension: String?= null
) : EpisodeDetailsResponse