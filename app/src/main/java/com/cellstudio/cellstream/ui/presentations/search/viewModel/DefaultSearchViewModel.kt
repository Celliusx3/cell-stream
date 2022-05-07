package com.cellstudio.cellstream.ui.presentations.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cellstudio.cellstream.data.base.models.request.SearchRequest
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultSearchViewModel @Inject constructor(private val sourceRepository: SourceRepository, stateHandle: SavedStateHandle) : BaseViewModel(stateHandle), SearchViewModel {
    private var nextPage: Int = 1

    private val _videos = MutableLiveData<MutableList<VideoResponse>>().apply { value = mutableListOf() }
    override val videos: LiveData<MutableList<VideoResponse>> = _videos

    private val _refreshLoading: MutableLiveData<Boolean> = MutableLiveData()
    override val refreshLoading: LiveData<Boolean> = _refreshLoading

    private val _paginationLoading: MutableLiveData<Boolean> = MutableLiveData()
    override val paginationLoading: LiveData<Boolean> = _paginationLoading

    override val query: MutableLiveData<String> = MutableLiveData<String>()

    override fun onRefresh() {
        nextPage = 1
        onGetSearchResults()
    }

    override fun onLoadMore() {
        onGetSearchResults()
    }

    override fun onSearch(query: String) {
        this.query.value = query
        onRefresh()
    }

    private fun onGetSearchResults() {
        viewModelScope.launch {
            query.value?.let {
                nextPage.let { page ->
                    if (page <= 1) {
                        _refreshLoading.value = true
                    } else {
                        _paginationLoading.value = true
                    }
                    val response = safeApiCall { sourceRepository.search(SearchRequest(it, page)) }
                    response?.let { addSearchResults(page <= 1, it) }
                    nextPage += 1
                    _paginationLoading.value = false
                    _refreshLoading.value = false
                }
            }
        }
    }

    private fun addSearchResults(shouldRefresh: Boolean,orders: List<VideoResponse>) {
        val currentList = if (shouldRefresh) mutableListOf() else _videos.value?: mutableListOf()
        currentList.addAll(orders)
        _videos.value = currentList
    }

}