package com.thecode.infotify.core.data

import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.network.InfotifyRemoteDataSourceImpl
import com.thecode.infotify.database.AppDatabase
import com.thecode.infotify.database.article.ArticleEntity
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val networkDataSource: InfotifyRemoteDataSourceImpl,
    private val appDataBase: AppDatabase
) {

    private fun isArticleBookmarked(id: String): Boolean {
        return appDataBase.getArticlesDao().findByPrimaryId(id) != null
    }

    suspend fun fetchNews(query: String, language: String, sortBy: String): News {
        return networkDataSource.fetchNews(query, language, sortBy)
    }

    suspend fun fetchHeadlinesByLang(language: String): News {
        return networkDataSource.fetchTopHeadlinesByLanguage(language)
    }

    suspend fun fetchHeadlinesByLangAndCat(language: String, category: String): News {
        return networkDataSource.fetchTopHeadlinesByLanguageAndCategory(language, category)
    }

    fun getBookmarks(): List<ArticleEntity> {
        return appDataBase.getArticlesDao().getAllArticles()
    }

    fun saveBookmark(articleEntity: ArticleEntity){
        appDataBase.getArticlesDao().insert(articleEntity)
    }

    fun deleteBookmark(url: String){
        appDataBase.getArticlesDao().deleteByPrimaryId(url)
    }
}
