package com.project.news.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.project.news.R
import com.project.news.databinding.FragmentArticleBinding
import com.project.news.ui.activity.MainActivity
import com.project.news.ui.fragments.base.BaseFragment

/**
 * Loads article on webView from news url<field in Article> .
 * on saving article it will insert data in room database <article_db.db> .
 */
class ArticleFrag : BaseFragment(R.layout.fragment_article) {
    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragArgs by navArgs()

    override fun onStart() {
        super.onStart()
        val btmNav =
            (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.btm_nav)
        btmNav.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
            settings.loadsImagesAutomatically = true
            isClickable = true
        }

        binding.fab.setOnClickListener {
            newsViewModel.saveNews(article)
            Snackbar.make(
                binding.fab,
                getString(R.string.saved_success_message),
                Snackbar.LENGTH_SHORT
            ).setAnchorView(binding.fab).show()

        }
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY -> // the delay of the extension of the FAB is set for 12 items
            val extendedFloatingActionButton = binding.fab
            if (scrollY > oldScrollY + 12 && extendedFloatingActionButton.isExtended) {
                extendedFloatingActionButton.shrink()
            }

            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !extendedFloatingActionButton.isExtended) {
                extendedFloatingActionButton.extend()
            }

            // if the nestedScrollView is at the first item of the list then the
            // extended floating action should be in extended state
            if (scrollY == 0) {
                extendedFloatingActionButton.extend()
            }
        }
    }


}