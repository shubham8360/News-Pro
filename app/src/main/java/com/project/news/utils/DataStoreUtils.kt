package com.project.news.utils

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

class DataStoreUtils(val context: Context) {
    private val Context.dataStore by preferencesDataStore("app_preferences")

    companion object{

    }


 /*   private suspend fun save(key:String,value:String){
        val datastoreKey= stringPreferencesKey(key)
        getInstance()
    }*/
}