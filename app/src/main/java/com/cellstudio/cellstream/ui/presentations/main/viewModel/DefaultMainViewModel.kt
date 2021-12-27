package com.cellstudio.cellstream.ui.presentations.main.viewModel

import androidx.lifecycle.SavedStateHandle
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DefaultMainViewModel @Inject constructor(state: SavedStateHandle) : BaseViewModel(state), MainViewModel