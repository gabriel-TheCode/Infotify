package com.thecode.infotify.core.repositories

import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.remote.InfotifyRemoteDataSourceImpl
import com.thecode.infotify.database.AppDatabase
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.framework.datasource.network.mapper.ArticleMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val networkDataSource: InfotifyRemoteDataSourceImpl,
    private val articleMapper: ArticleMapper,
    private val appDataBase: AppDatabase
) {

    suspend fun fetchNews(query: String, language: String, sortBy: String): News {
        return networkDataSource.fetchNews(query, language, sortBy)
    }

    suspend fun fetchHeadlinesByLangAndCat(language: String, category: String): News {
        return networkDataSource.fetchTopHeadlinesByLanguageAndCategory(language, category)
    }

    fun getBookmarks(): Flow<List<ArticleEntity>> {
        return appDataBase.getArticlesDao().getAllArticles()
    }

    suspend fun saveBookmark(article: Article) {
        appDataBase.getArticlesDao().insert(articleMapper.mapToEntity(article))
    }

    suspend fun deleteBookmark(url: String) {
        appDataBase.getArticlesDao().deleteByPrimaryId(url)
    }
}
