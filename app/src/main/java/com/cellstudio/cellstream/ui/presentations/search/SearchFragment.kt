package com.cellstudio.cellstream.ui.presentations.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cellstudio.cellstream.R
import com.cellstudio.cellstream.data.base.models.response.VideoResponse
import com.cellstudio.cellstream.databinding.FragmentSearchBinding
import com.cellstudio.cellstream.ui.adapters.home.HomeAdapter
import com.cellstudio.cellstream.ui.presentations.base.BaseFragment
import com.cellstudio.cellstream.ui.presentations.details.DetailsFragment
import com.cellstudio.cellstream.ui.presentations.search.adapters.SearchAdapter
import com.cellstudio.cellstream.ui.presentations.search.viewModel.DefaultSearchViewModel
import com.cellstudio.cellstream.ui.utilities.UIVisibilityUtils
import com.cellstudio.cellstream.ui.views.EndlessRecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val viewModel: DefaultSearchViewModel by viewModels()

    private lateinit var adapter: SearchAdapter
    private lateinit var endlessScrollListener: EndlessRecyclerViewScrollListener

    override fun createBindings(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, attachToParent)
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        viewModel.videos.observe(viewLifecycleOwner) {
            adapter.setModels(it)
        }

        viewModel.refreshLoading.observe(viewLifecycleOwner) {
            if (it) {
                endlessScrollListener.resetState()
            }

            binding?.srlSearch?.isRefreshing = it
        }

        viewModel.paginationLoading.observe(viewLifecycleOwner) {
            adapter.setLoading(it)
        }
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        setupListener()
        setupAdapter()

        binding?.srlSearch?.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    private fun setupListener() {
        binding?.svSearchSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.onSearch(query)
                binding?.svSearchSearch?.let {
                    it.clearFocus()
                    UIVisibilityUtils.hideKeyboard(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //searchViewModel.quickSearch(newText)
                return true
            }
        })
    }

    private fun setupAdapter() {
        val spanCount = 2
        val layoutManager = GridLayoutManager(context, spanCount)
        adapter = SearchAdapter(listOf())
        adapter.listener = object: SearchAdapter.Listener {
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

        binding?.rvSearchResults?.addOnScrollListener(endlessScrollListener)
        binding?.rvSearchResults?.layoutManager = layoutManager
        binding?.rvSearchResults?.adapter = adapter
    }
}