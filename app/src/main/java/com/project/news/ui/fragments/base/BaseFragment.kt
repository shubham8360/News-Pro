package com.project.news.ui.fragments.base

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.project.news.R
import com.project.news.adapters.NewsAdapter
import com.project.news.db.ArticleDatabase
import com.project.news.models.Article
import com.project.news.ui.activity.MainActivity
import com.project.news.vm.NewsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *  Re-initialization already from already initialized members from activity, to access them in child fragments .
 */

open class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    var btmNav: BottomNavigationView? = null

    val newsViewModel: NewsViewModel by activityViewModels()
    val adapter: NewsAdapter by lazy { NewsAdapter() }

    @Inject
    lateinit var articleDatabase: ArticleDatabase

    var toolbar: MaterialToolbar? = null
    var fragment: String? = null

    override fun onStart() {
        super.onStart()
        btmNav =
            (requireActivity() as MainActivity).findViewById(R.id.btm_nav)
        btmNav?.visibility = View.VISIBLE
        toolbar =
            (requireActivity() as MainActivity).findViewById(R.id.toolbar)
        toolbar?.menu?.setGroupVisible(R.id.search_container, false)
        setUpMenuItemListeners()
        enableSelection(false)
    }


    private fun setUpMenuItemListeners() {
        toolbar?.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.cancel -> {
                    adapter.tracker?.clearSelection()
                    true
                }

                R.id.save -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val tempList = adapter.tracker?.selection?.map {
                            adapter.currentList[it.toInt()]
                        }
                        newsViewModel.insertAll(tempList as ArrayList<Article>)
                        withContext(Dispatchers.Main) {
                            adapter.tracker?.clearSelection()
                            enableSelection(false)
                            Toast.makeText(
                                requireContext(),
                                R.string.saved_success_message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    true
                }

                R.id.delete -> {
                    val tempList =
                        adapter.tracker?.selection?.map { adapter.currentList[it.toInt()] }
                    newsViewModel.deleteListOfNews(tempList as ArrayList<Article>)
                    enableSelection(false)
                    Snackbar.make(
                        requireView(),
                        "${tempList.size} articles deleted successfully.",
                        Snackbar.LENGTH_SHORT
                    ).setAnchorView(btmNav).show()
                    adapter.tracker?.clearSelection()
                    true
                }


                R.id.settings -> {
                    findNavController().navigate(R.id.settingsFragment)
                    true
                }

                else -> false
            }
        }
    }

    fun enableSelection(enable: Boolean) {
        if (fragment == getString(R.string.saved_news)) {
            (requireActivity() as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar).menu.setGroupVisible(
                R.id.selection_delete,
                enable
            )

        } else {
            (requireActivity() as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar).menu.setGroupVisible(
                R.id.selection_type,
                enable
            )
        }
        if (!enable) {
            toolbar?.title = fragment
            adapter.tracker?.clearSelection()
        }
    }

}