package com.cellstudio.cellstream.ui.presentations.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel

interface SearchViewModel : ViewModel {
    val refreshLoading: LiveData<Boolean>
    val paginationLoading: LiveData<Boolean>
    val query: MutableLiveData<String>
    val videos: LiveData<MutableList<VideoResponse>>

    fun onRefresh()
    fun onLoadMore()
    fun onSearch(query: String)
}
