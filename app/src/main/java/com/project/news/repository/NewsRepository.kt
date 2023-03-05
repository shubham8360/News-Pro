package com.project.news.repository

import com.project.news.api.NewsApi
import com.project.news.db.ArticleDatabase
import com.project.news.models.Article
import javax.inject.Inject

/**
 * Repository pattern implemented in MVVM .
 * For ViewModel it provides data ,i.e ViewModel unaware from where we are getting data either from remote or local. (Abstraction)
 * It handles all operation either to database or api to get response
 * */

class NewsRepository @Inject constructor(
    private val db: ArticleDatabase,
    private val serviceApi: NewsApi
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        serviceApi.getBreakingNews(countryCode, pageNumber)

    suspend fun getSearchNews(searchQuery: String, pageNumber: Int) =
        serviceApi.searchForNews(searchQuery, pageNumber)

    fun getSavedNews() = db.getArticleDao().getArticles()

    suspend fun saveNews(article: Article) = db.getArticleDao().upsert(article)

    suspend fun deleteNews(article: Article) = db.getArticleDao().deleteArticle(article)
    suspend fun deleteListOfArticles(list: ArrayList<Article>) =
        db.getArticleDao().deleteListOfArticle(list)

    suspend fun insertAll(list: ArrayList<Article>) = db.getArticleDao().insertAll(list)

}