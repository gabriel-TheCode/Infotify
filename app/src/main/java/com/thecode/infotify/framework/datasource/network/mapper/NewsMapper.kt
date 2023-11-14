package com.thecode.infotify.framework.datasource.network.mapper

import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.domain.SourceItem
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import javax.inject.Inject

class NewsMapper @Inject constructor() :
    EntityMapper<NewsObjectResponse, News> {
    override fun mapToDomain(entity: NewsObjectResponse): News {
        return News(
            entity.status.toString(),
            entity.totalResults.toString(),
            entity.articles.map {
                mapFromNewsItems(it)
            }
        )
    }

    private fun mapFromNewsItems(article: NewsObjectResponse.Result): Article {
        return Article(
            SourceItem(
                article.source.id,
                article.source.name.toString()
            ),
            article.author,
            article.title,
            article.description,
            article.url,
            article.urlToImage,
            article.publishedAt,
            article.content
        )
    }

    fun newsEntityToItem(article: ArticleEntity): Article {
        return Article(
            SourceItem(article.source.id, article.source.name.toString()),
            article.author,
            article.title,
            article.description,
            article.url,
            article.urlToImage,
            article.publishedAt,
            article.content
        )
    }

    override fun mapToEntity(domainModel: News): NewsObjectResponse {
        throw Exception("Not used")
    }
}
