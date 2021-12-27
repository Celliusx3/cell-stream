package com.cellstudio.cellstream.ui.presentations.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.R
import com.cellstudio.cellstream.data.base.models.entities.Genre
import com.cellstudio.cellstream.data.base.models.response.EpisodeResponse
import com.cellstudio.cellstream.databinding.FragmentDetailsBinding
import com.cellstudio.cellstream.ui.adapters.details.EpisodeAdapter
import com.cellstudio.cellstream.ui.presentations.base.BaseFragment
import com.cellstudio.cellstream.ui.presentations.details.viewModel.DefaultDetailsViewModel
import com.cellstudio.cellstream.ui.presentations.player.PlayerFragment
import com.cellstudio.cellstream.ui.utilities.ImageUtils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {
    override val viewModel: DefaultDetailsViewModel by viewModels()

    private lateinit var adapter: EpisodeAdapter

    override fun createBindings(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): FragmentDetailsBinding {
        return FragmentDetailsBinding.inflate(inflater, container, attachToParent)
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        binding?.tbDetails?.toolbar?.setupWithNavController(navController, appBarConfiguration)
        binding?.tbDetails?.toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding?.tbDetails?.toolbar?.title = null
        setupAdapter()
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        viewModel.image.observe(viewLifecycleOwner) { image ->
            binding?.ivDetailsImage?.let {
                ImageUtils.setImageUri(it, image)
            }
        }

        viewModel.tags.observe(viewLifecycleOwner) { tags ->
            binding?.cgDetailsTags?.removeAllViews()
            tags.forEach {
                setupGenreTags(it, binding?.cgDetailsTags)
            }
        }

        viewModel.isShowTags.observe(viewLifecycleOwner) {
            binding?.llDetailsGenre?.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.title.observe(viewLifecycleOwner) {
            binding?.tvDetailsTitle?.text = it
        }

        viewModel.synopsis.observe(viewLifecycleOwner) {
            binding?.tvDetailsSynopsis?.text = it
        }

        viewModel.isShowSynopsis.observe(viewLifecycleOwner) {
            binding?.llDetailsSynopsis?.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.navigateToPlayer.asLiveData().observe(viewLifecycleOwner) {
            navController.navigate(R.id.action_to_navigation_player, PlayerFragment.newInstance(it))
        }

        viewModel.episodes.observe(viewLifecycleOwner) {
            adapter.refreshData(it)
        }
    }


    private fun setupAdapter() {
        adapter = EpisodeAdapter(listOf())
        adapter.listener = object: EpisodeAdapter.Listener {
            override fun onClick(model: EpisodeResponse) {
                viewModel.onPlayEpisodeClicked(model)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding?.rvDetailsEpisodes?.layoutManager = layoutManager
        binding?.rvDetailsEpisodes?.adapter = adapter
    }

    private fun setupGenreTags(model: Genre, chipGroup: ChipGroup?) {
        val chip = layoutInflater.inflate(R.layout.item_layout_chip, null, false) as Chip
        chip.text = model.title
        chipGroup?.addView(chip)
    }

    companion object {
        // Number of seconds to seek fw bw when FF or RW is pressed.
        const val EXTRA_DETAILS = "extra_details"
        fun newInstance(data: String): Bundle {
            return Bundle().apply {
                putString(EXTRA_DETAILS, data)
            }
        }
    }
}