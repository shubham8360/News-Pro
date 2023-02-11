package com.project.news

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.onesignal.OneSignal
import com.project.news.constants.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {

    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(applicationContext)
        OneSignal.setAppId(Constants.ONE_SIGNAL_APP_ID)
        OneSignal.promptForPushNotifications()
    }

}