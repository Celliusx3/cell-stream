package com.cellstudio.cellstream.ui.presentations.settings

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.cellstudio.cellstream.MainActivity
import com.cellstudio.cellstream.databinding.FragmentSettingsBinding
import com.cellstudio.cellstream.ui.presentations.action.ActionFragment
import com.cellstudio.cellstream.ui.presentations.action.models.ActionModel
import com.cellstudio.cellstream.ui.presentations.base.BaseFragment
import com.cellstudio.cellstream.ui.presentations.settings.models.SourceModel
import com.cellstudio.cellstream.ui.presentations.settings.viewModel.DefaultSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override val viewModel: DefaultSettingsViewModel by viewModels()

    override fun createBindings(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, attachToParent)
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        viewModel.restart.asLiveData().observe(viewLifecycleOwner) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            requireActivity().startActivity(intent)
            Runtime.getRuntime().exit(0)
        }
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        binding?.tvSettingsSource?.text = viewModel.source
        binding?.llSettingsSource?.setOnClickListener {
            val fragment = ActionFragment()
            fragment.setActions(viewModel.sources)
            fragment.listener = object: ActionFragment.Listener {
                override fun onClicked(model: ActionModel) {
                    if (model is SourceModel)
                        viewModel.onSourceSelected(model)
                }
            }
            fragment.show(childFragmentManager, "Sources")
        }
    }
}