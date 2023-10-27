package com.project.news.utils

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.project.news.ui.fragments.DAY_MODE
import com.project.news.ui.fragments.NIGHT_MODE

fun Fragment.applyTheme(theme: String) {
    requireContext().applyTheme(theme)
}

fun Context.applyTheme(theme: String) {
    when (theme) {
        DAY_MODE -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        NIGHT_MODE -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        else -> {
            runOnApiAbove(28, {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }, otherwise = {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            })
        }
    }
}

inline fun runOnApiAbove(api: Int, f: () -> Unit, otherwise: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT > api) {
        f()
    } else {
        otherwise()
    }
}