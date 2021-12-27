package com.cellstudio.cellstream.ui.presentations.action

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.databinding.FragmentActionBinding
import com.cellstudio.cellstream.databinding.FragmentDetailsBinding
import com.cellstudio.cellstream.ui.presentations.action.adapters.ActionAdapter
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel
import com.cellstudio.cellstream.ui.presentations.action.viewModel.DefaultActionViewModel
import com.cellstudio.cellstream.ui.presentations.base.BaseBottomSheetDialogFragment
import com.cellstudio.cellstream.ui.presentations.base.viewModel.ViewModel
import com.cellstudio.cellstream.ui.presentations.settings.viewModel.DefaultSettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActionFragment : BaseBottomSheetDialogFragment<FragmentActionBinding>() {
    private var allActions: List<ActionModel> = emptyList()
    private lateinit var adapter: ActionAdapter

    var listener: Listener?= null

    override val viewModel: DefaultActionViewModel by viewModels()

    interface Listener {
        fun onClicked(model: ActionModel)
    }

    override fun createBindings(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): FragmentActionBinding {
        return FragmentActionBinding.inflate(inflater, container, attachToParent)
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = ActionAdapter(allActions)
        adapter.listener = object: ActionAdapter.Listener {
            override fun onClick(model: ActionModel) {
                listener?.onClicked(model)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding?.rvSingleSelectionMain?.layoutManager = layoutManager
        binding?.rvSingleSelectionMain?.adapter = adapter
    }

    fun setActions(actionModels: List<ActionModel>) {
        this.allActions = actionModels
        if (::adapter.isInitialized) {
            adapter.models = actionModels
            adapter.notifyDataSetChanged()
        }
    }
}