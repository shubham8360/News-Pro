package com.project.news.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.news.models.Article

/**
 * Database Access operation (DAO) defined here, with Room Library
 * */
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: ArrayList<Article>)

    @Query("select * from articles")
    fun getArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Delete
    suspend fun deleteListOfArticle(list: ArrayList<Article>)

    @Query("select count() from articles where url=:url")
    suspend fun getCount(url: String): Long

}