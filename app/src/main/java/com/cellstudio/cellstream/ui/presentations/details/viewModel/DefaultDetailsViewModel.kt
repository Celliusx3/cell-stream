package com.cellstudio.cellstream.ui.presentations.details.viewModel

import androidx.lifecycle.*
import com.cellstudio.cellstream.data.base.models.entities.Genre
import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import com.cellstudio.cellstream.ui.presentations.details.DetailsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel

class DefaultDetailsViewModel @Inject constructor(private val sourceRepository: SourceRepository, stateHandle: SavedStateHandle) : BaseViewModel(stateHandle), DetailsViewModel {
    private val _details: MutableLiveData<VideoDetailsResponse> = MutableLiveData()
    override val image: LiveData<String> = Transformations.map(_details) { it.image }
    override val title: LiveData<String> = Transformations.map(_details) { it.title }
    override val synopsis: LiveData<String> = Transformations.map(_details) { it.synopsis }
    override val isShowSynopsis: LiveData<Boolean> = Transformations.map(_details) { !it.synopsis.isNullOrEmpty() }
    override val tags: LiveData<List<Genre>> = Transformations.map(_details) { it.genres?: listOf() }
    override val isShowTags: LiveData<Boolean> = Transformations.map(_details) { !it.genres.isNullOrEmpty() }
    override val episodes: LiveData<List<EpisodeResponse>> = Transformations.map(_details) { it.episodes }
    private val _navigateToPlayer: MutableSharedFlow<String> = MutableSharedFlow()
    override val navigateToPlayer: SharedFlow<String> = _navigateToPlayer
    private var id: String?= stateHandle.get(DetailsFragment.EXTRA_DETAILS)

    init {
        onRefresh()
    }

    override fun onRefresh() {
        onGetDetails()
    }

    override fun onPlayClicked() {
        viewModelScope.launch {
            _details.value?.id?.let {
                _navigateToPlayer.emit(it)
            }
        }
    }

    override fun onPlayEpisodeClicked(episode: EpisodeResponse) {
        viewModelScope.launch {
            _navigateToPlayer.emit(episode.id)
        }
    }

    private fun onGetDetails() {
        viewModelScope.launch {
            id?.let {
                val response = safeApiCall { sourceRepository.getVideoDetails(it) }
                response?.let { _details.value = it }
            }
        }
    }
}