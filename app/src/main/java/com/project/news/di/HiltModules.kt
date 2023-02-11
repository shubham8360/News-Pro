package com.project.news.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.project.news.api.NewsApi
import com.project.news.constants.Constants
import com.project.news.db.ArticleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModules {

    @Singleton
    @Provides
    fun provideDB(@ApplicationContext context: Context): ArticleDatabase {
        return Room.databaseBuilder(
            context.applicationContext, ArticleDatabase
            ::class.java, Constants.DATABASE
        ).build()
    }

    @Singleton
    @Provides
    fun provideServiceApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Singleton
    @Provides
    fun getSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}