package com.cellstudio.cellstream.data.source.example.kitsu.repositories

import com.cellstudio.cellstream.data.base.models.entities.Filter
import com.cellstudio.cellstream.data.base.models.request.SearchRequest
import com.cellstudio.cellstream.data.base.models.request.VideosRequest
import com.cellstudio.cellstream.data.base.models.response.EpisodeDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.data.base.repositories.source.BaseSourceRepository
import com.cellstudio.cellstream.data.services.network.NetworkService
import com.cellstudio.cellstream.data.source.example.kitsu.models.entities.KitsuVideo
import com.cellstudio.cellstream.data.source.example.kitsu.models.entities.KitsuVideoDetails
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuEpisodesResponse
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuVideoResponse
import com.cellstudio.cellstream.data.source.example.kitsu.models.response.KitsuVideosResponse
import com.cellstudio.cellstream.data.source.example.tmdb.models.entities.TmdbEpisodeDetails
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class KitsuRepository(networkService: NetworkService) : BaseSourceRepository(networkService) {
    override val name: String = "Kitsu"
    private val url: String = "https://kitsu.io"

    private val json: Json = Json { ignoreUnknownKeys = true }

    override val filters: List<Filter> = listOf()

    override fun getHome(request: VideosRequest): List<VideoResponse> {
        val link = "$url/api/edge/trending/anime"
        val response = getResponse(link)

        val kitsu = json.decodeFromString<KitsuVideosResponse>(response)
        return kitsu.data.map { KitsuVideo.create(it) }
    }

    override fun search(request: SearchRequest): List<VideoResponse> {
        throw Exception("Testing")
//        val link = "$url/3/search/movie"
//        val params = hashMapOf(
//            Pair(apiKeyQuery, apiKey),
//            Pair("query", request.query),
//            Pair("page", (request.page?: 1).toString())
//        )
//        val response = getResponse(link, params)
//        val tmdbs = json.decodeFromString<TmdbVideosResponse>(response)
//        return tmdbs.results.map { TmdbVideo.create(it, imageUrl) }
    }

    override fun getVideoDetails(id: String): VideoDetailsResponse {
        val link = "$url/api/edge/anime/$id"
        val response = getResponse(link)

        val kitsu = json.decodeFromString<KitsuVideoResponse>(response)
        val trailerLink = "$url/api/edge/anime/$id/episodes"
        val trailerResponse = getResponse(trailerLink)
        val trailer = json.decodeFromString<KitsuEpisodesResponse>(trailerResponse)
        return KitsuVideoDetails.create(kitsu, trailer)
    }

    override fun getEpisodeDetails(id: String): EpisodeDetailsResponse {
        return TmdbEpisodeDetails("https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8")
    }
}