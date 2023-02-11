package com.project.news.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.news.models.Article


/**
* Database class is implemented with Room library.
* Singleton pattern implemented for database object for whole application.
*/

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao
}