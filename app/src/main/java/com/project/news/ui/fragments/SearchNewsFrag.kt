package com.project.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.project.news.R
import com.project.news.constants.Constants
import com.project.news.constants.Resource
import com.project.news.databinding.FragmentSearchNewsBinding
import com.project.news.ui.activity.MainActivity
import com.project.news.ui.fragments.base.BaseFragment
import com.project.news.utils.MyItemDetailsLookup
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fetches and List all articles related to search query and list them in recycler view.
 * Search query results may contain multiple pages, for that pagination implemented.
 * By default search query searches in all parameters of Article (which includes source, desc, tittle etc).
 * */
private const val TAG = "SearchNewsFrag"

class SearchNewsFrag : BaseFragment(R.layout.fragment_search_news) {
    private var searchView: SearchView? = null
    private lateinit var binding: FragmentSearchNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNewsBinding.bind(view)
        setUpRecyclerView()
        setUpSelectionTracker()
        observer()
    }


    override fun onStart() {
        fragment = getString(R.string.search_news)
        super.onStart()
        toolbar?.menu?.setGroupVisible(R.id.search_container, true)
        searchView = toolbar?.menu?.getItem(0)?.actionView as SearchView
        searchListeners()
    }

    private fun setUpSelectionTracker() {
        adapter.tracker = SelectionTracker.Builder(
            "mySelection",
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
                    (requireActivity() as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar).menu.setGroupVisible(
                        R.id.selection_type,
                        true
                    )
                    toolbar?.title = items.toString()
                } else {
                    (requireActivity() as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar).menu.setGroupVisible(
                        R.id.selection_type,
                        false
                    )
                    toolbar?.title = getString(R.string.breaking_news)
                }
            }
        })
    }

    var job: Job? = null
    private fun searchListeners() {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newsViewModel.restoreSearchPageCount()
                newsViewModel.restoreSearchNewsResponse()
                job?.cancel()
                job = MainScope().launch {
                    delay(Constants.SEARCH_NEWS_TIME_DELAY)
                    newText?.let {
                        if (it.isNotEmpty()) {
                            newsViewModel.loadSearchResults(it)
                        }
                    }
                }
                return true
            }
        })
    }

    private fun observer() {
        newsViewModel.getSearchNews().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let { newsResponse ->
                        if (newsResponse.articles.size > 0) {
                            adapter.submitList(newsResponse.articles)
                            val totalPage = newsResponse.totalResults
                            isLastPage = newsViewModel.getSearchNewsPage() == totalPage
                            binding.apply {
                                rv.visibility = View.VISIBLE
                                lottieAnimation.visibility = View.GONE
                            }
                        } else {
                            binding.apply {
                                rv.visibility = View.GONE
                                lottieAnimation.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                    response.message?.let { message ->
                        Log.e(TAG, "observer: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }

    }

    private fun setUpRecyclerView() {
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        adapter.onItemClickListener = {
            val action = SearchNewsFragDirections.actionSearchNewsFragToArticleFrag(it)
            findNavController().navigate(action)
        }
        binding.rv.addOnScrollListener(this@SearchNewsFrag.scrollListener)
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.loadSearchResults(searchView?.query.toString())
                isScrolling = false
            } else {
                binding.rv.setPadding(0, 0, 0, 0)
            }
        }
    }

}