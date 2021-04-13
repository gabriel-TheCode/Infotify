package com.thecode.infotify.framework.datasource.network.mapper

import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.SourceItem
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import javax.inject.Inject

class NewsMapper @Inject constructor() :
    EntityMapper<NewsObjectResponse, News> {
    override fun mapToDomain(entity: NewsObjectResponse): News {
        return News(
            entity.status,
            entity.totalResults,
            entity.articles.map {
                mapFromNewsItems(it.article)
            })

    }

    private fun mapFromNewsItems(article: NewsObjectResponse.Result.Article): Article {
        return Article(
            mapFromSource(article),
            article.author,
            article.title,
            article.description,
            article.url,
            article.urlToImage,
            article.publishedAt,
            article.content
        )
    }

    private fun mapFromSource(article: NewsObjectResponse.Result.Article): SourceItem {
        return SourceItem(
            article.source?.id,
            article.source?.name
        )
    }

    override fun mapToEntity(domainModel: News): NewsObjectResponse {
        TODO("Not yet implemented")
    }
}