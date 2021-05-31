package com.thecode.infotify.framework.datasource.network.mapper

import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.SourceItem
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.database.source.SourceEntity
import javax.inject.Inject

class
ArticleMapper @Inject constructor() :
    EntityMapper<ArticleEntity, Article> {

    override fun mapToEntity(domainModel: Article): ArticleEntity {
        return ArticleEntity(
            sourceItemToEntity(domainModel.source),
            domainModel.author,
            domainModel.title,
            domainModel.description,
            domainModel.url,
            domainModel.urlToImage,
            domainModel.publishedAt,
            domainModel.content
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

    override fun mapToDomain(entity: ArticleEntity): Article {
        TODO("Not yet implemented")
    }

}