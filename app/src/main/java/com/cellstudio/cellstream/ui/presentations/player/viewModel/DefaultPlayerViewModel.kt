package com.cellstudio.cellstream.ui.presentations.player.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.cellstudio.cellstream.data.base.models.response.EpisodeDetailsResponse
import com.cellstudio.cellstream.data.base.models.response.VideoDetailsResponse
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import com.cellstudio.cellstream.ui.presentations.player.PlayerFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultPlayerViewModel @Inject constructor(private val sourceRepository: SourceRepository, stateHandle: SavedStateHandle) : BaseViewModel(stateHandle), PlayerViewModel {
    private val id = stateHandle.get<String>(PlayerFragment.EXTRA_DETAILS)

    private val _videoDetails: MutableLiveData<EpisodeDetailsResponse> = MutableLiveData()
    override val url: LiveData<String> = Transformations.map(_videoDetails) {
        it.url
    }

    init {
        viewModelScope.launch {
            id?.let {
                val data = safeApiCall { sourceRepository.getEpisodeDetails(it) }
                data?.let {
                    Log.d(TAG, it.url)
                    _videoDetails.value = it
                }
            }
        }
    }
}