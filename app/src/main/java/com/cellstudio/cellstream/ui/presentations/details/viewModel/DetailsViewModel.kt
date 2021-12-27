package com.cellstudio.cellstream.ui.presentations.details.viewModel

import androidx.lifecycle.LiveData
import com.cellstudio.cellstream.data.base.models.entities.Genre
import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel
import kotlinx.coroutines.flow.SharedFlow

interface DetailsViewModel : ViewModel {
    val image: LiveData<String>
    val title: LiveData<String>
    val synopsis: LiveData<String>
    val isShowSynopsis: LiveData<Boolean>
    val episodes: LiveData<List<EpisodeResponse>>
    val tags: LiveData<List<Genre>>
    val isShowTags: LiveData<Boolean>
    val navigateToPlayer: SharedFlow<String>
    fun onRefresh()
    fun onPlayClicked()
    fun onPlayEpisodeClicked(episode: EpisodeResponse)
}