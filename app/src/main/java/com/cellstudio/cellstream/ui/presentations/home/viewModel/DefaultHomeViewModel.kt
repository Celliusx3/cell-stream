package com.cellstudio.cellstream.ui.presentations.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cellstudio.cellstream.data.base.models.entities.Filter
import com.cellstudio.cellstream.data.base.models.request.VideoListRequest
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.data.base.repositories.config.ConfigRepository
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultHomeViewModel @Inject constructor(private val sourceRepository: SourceRepository, stateHandle: SavedStateHandle) : BaseViewModel(stateHandle), HomeViewModel {
    override val title: String = sourceRepository.name

    private val _items: MutableLiveData<MutableList<VideoResponse>> = MutableLiveData()
    override val items: LiveData<MutableList<VideoResponse>> = _items

    private val _paginationLoading: MutableLiveData<Boolean> = MutableLiveData()
    override val paginationLoading: LiveData<Boolean> = _paginationLoading

    private val _refreshLoading: MutableLiveData<Boolean> = MutableLiveData()
    override val refreshLoading: LiveData<Boolean> = _refreshLoading

    override val filters: List<Filter> = sourceRepository.filters

    private var selectedFilter: Filter?= null
    private var nextPage: Int = 1

    init {
        onRefresh()
    }

    private fun onGetHome() {
        viewModelScope.launch {
            nextPage.let { page ->
                if (page <= 1) {
                    _refreshLoading.value = true
                } else {
                    _paginationLoading.value = true
                }
                val request = VideoListRequest(genre = selectedFilter?.id, page = nextPage)
                val response = safeApiCall { sourceRepository.getHome(request) }
                response?.let { addVideos(page <= 1, it) }
                nextPage += 1
                _paginationLoading.value = false
                _refreshLoading.value = false
            }
        }
    }

    private fun addVideos(shouldRefresh: Boolean, response: List<VideoResponse>) {
        val currentList = if (shouldRefresh) mutableListOf() else _items.value?: mutableListOf()
        currentList.addAll(response)
        _items.value = currentList
    }

    override fun onRefresh() {
        nextPage = 1
        onGetHome()
    }

    override fun onApplyFilter(filter: Filter) {
        this.selectedFilter = filter
        onRefresh()
    }

    override fun onLoadMore() {
        onGetHome()
    }
}