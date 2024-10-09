package com.thecode.infotify.data.remote.mapper

import com.thecode.infotify.data.local.model.article.ArticleEntity
import com.thecode.infotify.data.local.model.source.SourceEntity
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.SourceItem
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

    private fun sourceItemToEntity(source: SourceItem): SourceEntity {
        return SourceEntity(
            source.id.toString(),
            source.name
        )
    }

    override fun mapToDomain(entity: ArticleEntity): Article {
        TODO("Not yet implemented")
    }
}
