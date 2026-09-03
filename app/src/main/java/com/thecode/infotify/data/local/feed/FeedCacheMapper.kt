package com.thecode.infotify.data.local.feed

import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.ArticleSource
import com.thecode.infotify.domain.model.Topic
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject

class FeedCacheMapper @Inject constructor() {

    fun toEntities(feedKey: String, articles: List<Article>, cachedAt: Long) =
        articles.mapIndexed { index, article ->
            CachedArticleEntity(
                feedKey = feedKey,
                url = article.url,
                position = index,
                id = article.id,
                title = article.title,
                description = article.description,
                imageUrl = article.imageUrl,
                publishedAt = article.publishedAt.toString(),
                sourceId = article.source.id,
                sourceName = article.source.name,
                sourceIconUrl = article.source.iconUrl,
                categories = article.categories.joinToString(",") { it.apiValue },
                cachedAt = cachedAt
            )
        }

    fun toDomain(entity: CachedArticleEntity): Article = Article(
        id = entity.id,
        title = entity.title,
        description = entity.description,
        url = entity.url,
        imageUrl = entity.imageUrl,
        publishedAt = parseInstant(entity.publishedAt),
        source = ArticleSource(
            id = entity.sourceId,
            name = entity.sourceName,
            iconUrl = entity.sourceIconUrl
        ),
        categories = entity.categories
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull(Topic::fromApiValue)
    )

    private fun parseInstant(raw: String): Instant = try {
        Instant.parse(raw)
    } catch (e: DateTimeParseException) {
        Instant.EPOCH
    }
}
