package com.project.news.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.color.DynamicColors
import com.project.news.R
import com.project.news.databinding.ActivityMainBinding
import com.project.news.vm.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * News Application based on MVVM architecture,& which uses jetpack components to ease development process .
 * Api used from https://newsapi.org/ .
 * Components used : Navigation Component, Room Database Library, LiveData, Retrofit, ViewModels ,Hilt Dagger etc.
 * Permission used: ACCESS_NETWORK_STATE, android.permission.INTERNET, android.permission.CHANGE_NETWORK_STATE
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val _newsViewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        DynamicColors.applyToActivityIfAvailable(this)
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
        binding.containerMain.toolbar.run {
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