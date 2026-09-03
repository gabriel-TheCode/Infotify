package com.thecode.infotify.data.remote.infotify

import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.ArticleSource
import com.thecode.infotify.domain.model.Topic
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * Maps the proxy envelope to domain articles.
 *
 * Deliberately thin: deduplication, date normalisation and the dropping of unusable
 * entries now happen on the server. What remains here is defensive parsing, because a
 * client should never crash on a payload it did not expect.
 */
class FeedMapper @Inject constructor() {

    fun toDomain(response: FeedResponse): ArticlePage = ArticlePage(
        articles = response.articles.orEmpty().mapNotNull(::toDomain),
        nextCursor = response.nextCursor?.takeIf { it.isNotBlank() }
    )

    private fun toDomain(dto: ArticleDto): Article? {
        val url = dto.url?.takeIf { it.isNotBlank() } ?: return null
        val title = dto.title?.takeIf { it.isNotBlank() } ?: return null
        val publishedAt = parseInstant(dto.publishedAt) ?: return null

        return Article(
            id = dto.id?.takeIf { it.isNotBlank() } ?: url,
            title = title,
            description = dto.description?.takeIf { it.isNotBlank() },
            url = url,
            imageUrl = dto.imageUrl?.takeIf { it.isNotBlank() },
            publishedAt = publishedAt,
            source = ArticleSource(
                id = dto.source?.id.orEmpty(),
                name = dto.source?.name?.takeIf { it.isNotBlank() } ?: UNKNOWN_SOURCE,
                iconUrl = dto.source?.iconUrl?.takeIf { it.isNotBlank() }
            ),
            categories = dto.categories.orEmpty().mapNotNull(Topic::fromApiValue)
        )
    }

    private fun parseInstant(raw: String?): Instant? {
        if (raw.isNullOrBlank()) return null
        return try {
            Instant.parse(raw)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private companion object {
        const val UNKNOWN_SOURCE = "—"
    }
}
