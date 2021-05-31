package com.thecode.infotify.core.repositories

import androidx.lifecycle.LiveData
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.remote.InfotifyRemoteDataSourceImpl
import com.thecode.infotify.database.AppDatabase
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.framework.datasource.network.mapper.ArticleMapper
import com.thecode.infotify.framework.datasource.network.mapper.NewsMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val networkDataSource: InfotifyRemoteDataSourceImpl,
    private val articleMapper: ArticleMapper,
    private val newsMapper: NewsMapper,
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
