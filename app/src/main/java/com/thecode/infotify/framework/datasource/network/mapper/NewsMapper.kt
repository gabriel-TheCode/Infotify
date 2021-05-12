package com.thecode.infotify.framework.datasource.network.mapper

import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.SourceItem
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.database.source.SourceEntity
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

    private fun newsItemToEntity(article: Article): ArticleEntity {
        return ArticleEntity(
            article.source?.let { sourceItemToEntity(it) },
            article.author,
            article.title,
            article.description,
            article.url.toString(),
            article.urlToImage,
            article.publishedAt,
            article.content
        )
    }

    private fun sourceItemToEntity(source: SourceItem): SourceEntity{
        return SourceEntity(
            source.id.toString(),
            source.cnbc,
            source.name,
            source.description,
            source.url,
            source.category,
            source.language,
            source.country
        )
    }

    override fun mapToEntity(domainModel: News): NewsObjectResponse {
        TODO("Not yet implemented")
    }
}