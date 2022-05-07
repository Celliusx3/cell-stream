package com.cellstudio.cellstream.ui.presentations.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.R
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.databinding.FragmentHomeBinding
import com.cellstudio.cellstream.ui.adapters.home.HomeAdapter
import com.cellstudio.cellstream.ui.presentations.base.BaseFragment
import com.cellstudio.cellstream.ui.presentations.details.DetailsFragment
import com.cellstudio.cellstream.ui.presentations.home.viewModel.DefaultHomeViewModel
import com.cellstudio.cellstream.ui.views.EndlessRecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val viewModel: DefaultHomeViewModel by viewModels()

    override fun createBindings(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, attachToParent)
    }

    private lateinit var adapter: HomeAdapter
    private lateinit var endlessScrollListener: EndlessRecyclerViewScrollListener

    override fun onBindData(view: View) {
        super.onBindData(view)
        viewModel.items.observe(viewLifecycleOwner) {
            adapter.setModels(it)
        }

        viewModel.refreshLoading.observe(viewLifecycleOwner) {
            if (it) {
                endlessScrollListener.resetState()
            }

            binding?.srlHome?.isRefreshing = it
        }

        viewModel.paginationLoading.observe(viewLifecycleOwner) {
            adapter.setLoading(it)
        }
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        setupAdapter()

        binding?.tbHome?.toolbar?.title = viewModel.title

        binding?.srlHome?.setOnRefreshListener {
            viewModel.onRefresh()
        }

        viewModel.filters.forEach { filter ->
            binding?.tbHome?.toolbar?.menu?.add(filter.title)?.setOnMenuItemClickListener {
                viewModel.onApplyFilter(filter)
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun setupAdapter() {
        val spanCount = 2
        val layoutManager = GridLayoutManager(context, spanCount)
        adapter = HomeAdapter(listOf())
        adapter.listener = object: HomeAdapter.Listener {
            override fun onItemClicked(model: VideoResponse) {
                navController.navigate(R.id.action_to_navigation_details, DetailsFragment.newInstance(model.id))
            }
        }

        endlessScrollListener = object: EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (!adapter.getLoading()) {
                    viewModel.onLoadMore()
                }
            }
        }

        binding?.rvHome?.addOnScrollListener(endlessScrollListener)
        binding?.rvHome?.layoutManager = layoutManager
        binding?.rvHome?.adapter = adapter
    }
}