package com.cellstudio.cellstream.ui.presentations.base.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel(protected val stateHandle: SavedStateHandle) : androidx.lifecycle.ViewModel(),
    ViewModel {

    protected val TAG = this.javaClass.simpleName

    protected suspend fun <T> safeApiCall(apiCall: suspend () -> T): T? {
        return withContext(Dispatchers.IO) {
            try {
                apiCall.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}