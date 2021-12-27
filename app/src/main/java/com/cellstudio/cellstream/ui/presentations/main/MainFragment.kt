package com.cellstudio.cellstream.ui.presentations.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.cellstudio.cellstream.R
import com.cellstudio.cellstream.databinding.FragmentMainBinding
import com.cellstudio.cellstream.ui.presentations.base.BaseFragment
import com.cellstudio.cellstream.ui.presentations.home.HomeFragment
import com.cellstudio.cellstream.ui.presentations.main.adapters.MainFragmentStateAdapter
import com.cellstudio.cellstream.ui.presentations.main.viewModel.DefaultMainViewModel
import com.cellstudio.cellstream.ui.presentations.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(){
    override val viewModel: DefaultMainViewModel by viewModels()

    override fun createBindings(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, attachToParent)
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        // Cancel ViewPager swipe
        binding?.vpMain?.isUserInputEnabled = false

        // Set viewpager adapter
        binding?.vpMain?.adapter = MainFragmentStateAdapter(listOf(HomeFragment(), SettingsFragment()), childFragmentManager, viewLifecycleOwner.lifecycle)

        // Listen bottom navigation tabs change
        binding?.bnvMain?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding?.vpMain?.setCurrentItem(0, false)
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_settings -> {
                    binding?.vpMain?.setCurrentItem(1, false)
                    return@setOnItemSelectedListener true
                }
            }

            false

        }
    }
}