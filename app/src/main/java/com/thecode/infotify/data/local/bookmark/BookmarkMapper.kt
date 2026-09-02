package com.thecode.infotify.data.local.bookmark

import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.ArticleSource
import com.thecode.infotify.domain.model.Category
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject

class BookmarkMapper @Inject constructor() {

    fun toEntity(article: Article, savedAt: Long): BookmarkEntity = BookmarkEntity(
        url = article.url,
        id = article.id,
        title = article.title,
        description = article.description,
        imageUrl = article.imageUrl,
        publishedAt = article.publishedAt.toString(),
        sourceId = article.source.id,
        sourceName = article.source.name,
        sourceIconUrl = article.source.iconUrl,
        categories = article.categories.joinToString(SEPARATOR) { it.apiValue },
        savedAt = savedAt
    )

    fun toDomain(entity: BookmarkEntity): Article = Article(
        id = entity.id,
        title = entity.title,
        description = entity.description,
        url = entity.url,
        imageUrl = entity.imageUrl,
        publishedAt = parseInstant(entity.publishedAt),
        source = ArticleSource(
            id = entity.sourceId,
            name = entity.sourceName.ifBlank { UNKNOWN_SOURCE },
            iconUrl = entity.sourceIconUrl
        ),
        categories = entity.categories
            .split(SEPARATOR)
            .filter { it.isNotBlank() }
            .mapNotNull(Category::fromApiValue)
    )

    /** Rows migrated from the NewsAPI era may carry an unparseable date; never crash on one. */
    private fun parseInstant(raw: String): Instant = try {
        Instant.parse(raw)
    } catch (e: DateTimeParseException) {
        Instant.EPOCH
    }

    private companion object {
        const val SEPARATOR = ","
        const val UNKNOWN_SOURCE = "—"
    }
}
