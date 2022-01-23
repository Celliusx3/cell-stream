package com.cellstudio.cellstream.ui.presentations.player.viewModel

import androidx.lifecycle.LiveData
import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel

interface PlayerViewModel : ViewModel {
    val episodeData: LiveData<Pair<String, String?>>
}