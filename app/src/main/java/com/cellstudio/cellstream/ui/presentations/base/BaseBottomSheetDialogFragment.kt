package com.cellstudio.cellstream.ui.presentations.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.cellstudio.cellstream.ui.presentations.action.viewModel.ActionViewModel
import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<T: ViewBinding>: BottomSheetDialogFragment() {
    protected abstract val viewModel: ViewModel
    protected val TAG = this.javaClass.simpleName

    protected var binding: T?= null

    protected abstract fun createBindings(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): T

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        binding = createBindings(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        onGetInputData()
        onBindView(view, savedInstanceState)
        onBindData(view)
    }

    protected open fun onBindView(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onBindView")
    }

    protected open fun onGetInputData() {
        Log.d(TAG, "onGetInputData")
    }

    protected open fun onBindData(view: View) {
        Log.d(TAG, "onBindData")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}