package com.thecode.infotify.data.remote.newsdata

import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.ArticleSource
import com.thecode.infotify.domain.model.Category
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * Turns the NewsData.io wire format into domain articles.
 *
 * Articles that cannot be rendered usefully are dropped rather than surfaced as blanks:
 * an entry with no link, no title or an unparseable date is not an article the app can
 * open, so it never reaches the UI.
 */
class NewsDataMapper @Inject constructor() {

    fun toDomain(response: NewsDataResponse): ArticlePage = ArticlePage(
        articles = response.results.orEmpty()
            .filterNot { it.duplicate == true }
            .mapNotNull(::toDomain)
            .distinctBy { it.url },
        nextCursor = response.nextPage
    )

    private fun toDomain(dto: NewsDataArticle): Article? {
        val url = dto.link?.takeIf { it.isNotBlank() } ?: return null
        val title = dto.title?.takeIf { it.isNotBlank() } ?: return null
        val publishedAt = parsePubDate(dto.pubDate) ?: return null

        return Article(
            id = dto.articleId ?: url,
            title = title,
            description = dto.description?.takeIf { it.isNotBlank() },
            url = url,
            imageUrl = dto.imageUrl?.takeIf { it.isNotBlank() },
            publishedAt = publishedAt,
            source = ArticleSource(
                id = dto.sourceId.orEmpty(),
                name = dto.sourceName?.takeIf { it.isNotBlank() } ?: UNKNOWN_SOURCE,
                iconUrl = dto.sourceIcon?.takeIf { it.isNotBlank() }
            ),
            categories = dto.category.orEmpty().mapNotNull(Category::fromApiValue)
        )
    }

    /**
     * NewsData.io sends "2026-09-02 05:41:00" in UTC — not ISO 8601, so [Instant.parse]
     * cannot be used directly.
     */
    private fun parsePubDate(raw: String?): Instant? {
        if (raw.isNullOrBlank()) return null
        return try {
            LocalDateTime.parse(raw, FORMATTER).toInstant(ZoneOffset.UTC)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        const val UNKNOWN_SOURCE = "—"
    }
}
