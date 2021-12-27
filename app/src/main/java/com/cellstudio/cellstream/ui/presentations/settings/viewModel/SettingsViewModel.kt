package com.cellstudio.cellstream.ui.presentations.settings.viewModel

import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel
import com.cellstudio.cellstream.ui.presentations.settings.models.SourceModel
import kotlinx.coroutines.flow.SharedFlow

interface SettingsViewModel : ViewModel {
    val source: String
    val sources: List<SourceModel>
    val restart: SharedFlow<Unit>

    fun onSourceSelected(source: SourceModel)
}