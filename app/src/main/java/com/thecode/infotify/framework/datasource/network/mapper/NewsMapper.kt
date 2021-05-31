package com.thecode.infotify.framework.datasource.network.mapper

import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.Source
import com.thecode.infotify.core.domain.SourceItem
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.database.source.SourceEntity
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import javax.inject.Inject

class NewsMapper @Inject constructor() :
    EntityMapper<NewsObjectResponse, News> {
    override fun mapToDomain(entity: NewsObjectResponse): News {
        return News(
            entity.status.toString(),
            entity.totalResults.toString(),
            entity.articles.map {
                mapFromNewsItems(it)}
            )
    }


    private fun mapFromNewsItems(article: NewsObjectResponse.Result): Article {
        return Article(
            SourceItem(
                article.source.id,
                article.source.name
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


    private fun newsItemToEntity(article: Article): ArticleEntity {
        return ArticleEntity(
            sourceItemToEntity(article.source),
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
            SourceItem(article.source?.id, article.source?.name),
            article.author,
            article.title,
            article.description,
            article.url,
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
        throw Exception("Not used")
    }
}