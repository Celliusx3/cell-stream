package com.cellstudio.cellstream.data.source.example.tmdb.repositories

import com.cellstudio.cellstream.data.base.models.entities.Filter
import com.cellstudio.cellstream.data.base.models.request.SearchRequest
import com.cellstudio.cellstream.data.base.models.request.VideosRequest
import com.cellstudio.cellstream.data.base.models.response.EpisodeDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.data.base.repositories.source.BaseSourceRepository
import com.cellstudio.cellstream.data.services.network.NetworkService
import com.cellstudio.cellstream.data.source.example.tmdb.models.entities.TmdbEpisodeDetails
import com.cellstudio.cellstream.data.source.example.tmdb.models.entities.TmdbVideo
import com.cellstudio.cellstream.data.source.example.tmdb.models.entities.TmdbVideoDetails
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbEpisodesResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbVideoDetailsResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.response.TmdbVideosResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TmdbRepository(networkService: NetworkService) : BaseSourceRepository(networkService) {
    override val name: String = "Tmdb"
    private val url: String = "https://api.themoviedb.org"
    private val imageUrl: String = "https://image.tmdb.org/t/p/original"

    private val apiKey = "PUT_YOUR_API_KEY_HERE"
    private val apiKeyQuery = "api_key"

    private val json: Json = Json { ignoreUnknownKeys = true }

    override val filters: List<Filter> = listOf(
        Filter("28", "Action"),
        Filter("12", "Adventure"),
        Filter("16", "Animation"),
        Filter("35", "Comedy"),
        Filter("80", "Crime"),
        Filter("99", "Documentary"),
        Filter("18", "Drama"),
        Filter("10751", "Family"),
        Filter("14", "Fantasy"),
        Filter("36", "History"),
        Filter("27", "Horror"),
        Filter("10402", "Music"),
        Filter("9648", "Mystery"),
        Filter("10749", "Romance"),
        Filter("878", "Science Fiction"),
        Filter("10770", "TV Movie"),
        Filter("53", "Thriller"),
        Filter("10752", "War"),
        Filter("37", "Western"),
    )

    override fun getHome(request: VideosRequest): List<VideoResponse> {
        val link = "$url/4/list/${request.genre?: filters[0].id}"
        val params = hashMapOf(
            Pair(apiKeyQuery, apiKey),
            Pair("page", (request.page?: 1).toString())
        )
        val response = getResponse(link, params)

        val tmdbs = json.decodeFromString<TmdbVideosResponse>(response)
        return tmdbs.results.map { TmdbVideo.create(it, imageUrl) }
    }

    override fun search(request: SearchRequest): List<VideoResponse> {
        val link = "$url/3/search/movie"
        val params = hashMapOf(
            Pair(apiKeyQuery, apiKey),
            Pair("query", request.query),
            Pair("page", (request.page?: 1).toString())
        )
        val response = getResponse(link, params)
        val tmdbs = json.decodeFromString<TmdbVideosResponse>(response)
        return tmdbs.results.map { TmdbVideo.create(it, imageUrl) }
    }

    override fun getVideoDetails(id: String): VideoDetailsResponse {
        val link = "$url/3/movie/$id"
        val params = hashMapOf(
            Pair(apiKeyQuery, apiKey),
        )
        val response = getResponse(link, params)

        val trailerLink = "$url/3/movie/$id/videos"
        val trailerParams = hashMapOf(
            Pair(apiKeyQuery, apiKey),
        )
        val trailerResponse = getResponse(trailerLink, trailerParams)

        val tmdb = json.decodeFromString<TmdbVideoDetailsResponse>(response)
        val trailer = json.decodeFromString<TmdbEpisodesResponse>(trailerResponse)

        return TmdbVideoDetails.create(tmdb, imageUrl, trailer)
    }

    override fun getEpisodeDetails(id: String): EpisodeDetailsResponse {
        return TmdbEpisodeDetails("https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8")
    }
}