package com.project.news.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.news.R
import com.project.news.ui.activity.MainActivity
import com.project.news.utils.applyTheme

const val THEME_KEY = "theme_key"
const val DAY_MODE = "day"
const val NIGHT_MODE = "night"
const val DEFAULT_MODE = "follow_system"

class SettingsFragment : PreferenceFragmentCompat() {

    private val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                THEME_KEY -> {
                    applyTheme(sharedPreferences.getString(THEME_KEY, DEFAULT_MODE) ?: DEFAULT_MODE)
                }
            }

        }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onStart() {
        super.onStart()
        val btmNav =
            (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.btm_nav)
        btmNav.visibility = View.GONE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}