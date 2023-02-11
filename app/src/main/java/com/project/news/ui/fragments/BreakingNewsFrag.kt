package com.project.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.news.R
import com.project.news.constants.Constants
import com.project.news.constants.Constants.Companion.COUNTRY_CODE_CONST
import com.project.news.constants.Constants.Companion.QUERY_PAGE_SIZE
import com.project.news.constants.Resource
import com.project.news.databinding.FragmentBreakingNewsBinding
import com.project.news.ui.fragments.base.BaseFragment
import com.project.news.utils.MyItemDetailsLookup

/**
 * Fetches and list top 20 news from first page, on scroll fetches again 20 news from next page, pagination concept implemented.
 */
private const val TAG = "BreakingNewsFrag"

class BreakingNewsFrag : BaseFragment(R.layout.fragment_breaking_news) {

    private lateinit var binding: FragmentBreakingNewsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBreakingNewsBinding.bind(view)
        setUpRecyclerView()
        setUpSelectionTracker()
        observers()
    }

    private fun setUpSelectionTracker() {
        adapter.tracker = SelectionTracker.Builder(
            Constants.SELECTION_ID,
            binding.rv,
            StableIdKeyProvider(binding.rv),
            MyItemDetailsLookup(binding.rv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        adapter.tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val items = adapter.tracker!!.selection.size()
                if (items > 0) {
                    enableSelection(true)
                    toolbar?.title = items.toString()
                } else {
                    enableSelection(false)
                }
            }
        })
    }

    override fun onStart() {
        fragment = getString(R.string.breaking_news)
        super.onStart()
    }



    private fun observers() {
        newsViewModel.getBreakingNews().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let {
                        adapter.submitList(it.articles)
                        val totalPage = it.totalResults
                        isLastPage = newsViewModel.getBreakingNewsPage() == totalPage
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                    response.message?.let { message ->
                        Log.e(TAG, "observers: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
        newsViewModel.loadBreakingNews(COUNTRY_CODE_CONST)
    }

    private fun setUpRecyclerView() {
        binding.rv.adapter = adapter
        binding.rv.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            addOnScrollListener(this@BreakingNewsFrag.scrollListener)
        }
        adapter.onItemClickListener = {
            val action = BreakingNewsFragDirections.actionBreakingNewsFragToArticleFrag(it)
            findNavController().navigate(action)
        }
    }

    private fun showProgress() {
        binding.progressCircular.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgress() {
        binding.progressCircular.visibility = View.GONE
        isLoading = false
    }

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.rv.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.loadBreakingNews(COUNTRY_CODE_CONST)
                isScrolling = false
            } else {
                binding.rv.setPadding(0, 0, 0, 0)
            }
        }
    }

}