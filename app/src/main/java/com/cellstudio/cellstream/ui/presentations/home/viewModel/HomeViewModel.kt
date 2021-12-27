package com.cellstudio.cellstream.ui.presentations.home.viewModel

import androidx.lifecycle.LiveData
import com.cellstudio.cellstream.data.base.models.entities.Filter
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel

interface HomeViewModel : ViewModel {
    val title: String
    val items: LiveData<MutableList<VideoResponse>>
    val filters: List<Filter>
    val refreshLoading: LiveData<Boolean>
    val paginationLoading: LiveData<Boolean>

    fun onApplyFilter(filter: Filter)
    fun onLoadMore()
    fun onRefresh()
}