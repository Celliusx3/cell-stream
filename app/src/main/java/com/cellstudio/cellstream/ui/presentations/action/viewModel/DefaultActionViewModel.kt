package com.cellstudio.cellstream.ui.presentations.action.viewModel

import androidx.lifecycle.SavedStateHandle
import com.cellstudio.cellstream.ui.presentations.base.viewModel.BaseViewModel
import javax.inject.Inject

class DefaultActionViewModel @Inject constructor(stateHandle: SavedStateHandle) : BaseViewModel(stateHandle), ActionViewModel