package com.cellstudio.cellstream.ui.presentations.settings.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cellstudio.cellstream.data.base.repositories.config.ConfigRepository
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import com.cellstudio.cellstream.ui.presentations.settings.models.SourceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaultSettingsViewModel @Inject constructor(private val settingsRepository: ConfigRepository, stateHandle: SavedStateHandle) : BaseViewModel(stateHandle), SettingsViewModel {
    override val source: String = settingsRepository.selectedSource.name
    override val sources: List<SourceModel> = settingsRepository.sources.map {
        SourceModel.create(it)
    }

    private val _restart = MutableSharedFlow<Unit>()
    override val restart: SharedFlow<Unit> = _restart

    override fun onSourceSelected(source: SourceModel) {
        viewModelScope.launch {
            settingsRepository.setSource(source.id)
            _restart.emit(Unit)
        }
    }
}