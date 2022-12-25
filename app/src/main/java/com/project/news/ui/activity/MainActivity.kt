package com.project.news.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.project.news.R
import com.project.news.databinding.ActivityMainBinding
import com.project.news.vm.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * News Application based on MVVM architecture,& which uses jetpack components to ease development process .
 * Api used from https://newsapi.org/ .
 * Components used : Navigation Component, Room Database Library, LiveData, Retrofit, ViewModels ,Hilt Dagger etc.
 * Permission used: ACCESS_NETWORK_STATE, android.permission.INTERNET, android.permission.CHANGE_NETWORK_STATE
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val _newsViewModel: NewsViewModel by lazy { ViewModelProvider(this)[NewsViewModel::class.java] }
    val newsViewModel: NewsViewModel get() = _newsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.containerMain.btmNav.setupWithNavController(findNavController(R.id.nav_host_fragment_content_main))
        setUpActionBar()
        networkAvailabilitySetup()
    }

    private fun setUpActionBar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.breakingNewsFrag,
                R.id.savedNewsFrag,
                R.id.searchNewsFrag
            )
        )
        binding.containerToolbar.toolbar.run {
            setupWithNavController(
                findNavController(R.id.nav_host_fragment_content_main),
                appBarConfiguration
            )
        }
    }

    /** UI handling on connection establish and lost */
    private fun networkAvailabilitySetup() {
        _newsViewModel.getConnectivity().observe(this) {
            if (it) {
                binding.containerLostConnection.root.visibility = View.GONE
                binding.containerMain.root.visibility = View.VISIBLE
            } else {
                binding.containerLostConnection.root.visibility = View.VISIBLE
                binding.containerMain.root.visibility = View.GONE
            }
        }
    }
}