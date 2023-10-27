package com.project.news

import android.app.Application
import android.content.SharedPreferences
import com.google.android.material.color.DynamicColors
import com.onesignal.OneSignal
import com.project.news.constants.Constants
import com.project.news.ui.fragments.DEFAULT_MODE
import com.project.news.ui.fragments.THEME_KEY
import com.project.news.utils.applyTheme
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NewsApplication : Application() {

    @Inject
    lateinit var sharedPreference:SharedPreferences

    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(applicationContext)
        OneSignal.setAppId(Constants.ONE_SIGNAL_APP_ID)
        OneSignal.promptForPushNotifications()

        applyTheme(sharedPreference.getString(THEME_KEY, DEFAULT_MODE)?: DEFAULT_MODE)
    }

}