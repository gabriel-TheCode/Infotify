package com.thecode.infotify.data

import com.thecode.infotify.data.remote.infotify.ArticleDto
import com.thecode.infotify.data.remote.infotify.FeedMapper
import com.thecode.infotify.data.remote.infotify.FeedResponse
import com.thecode.infotify.data.remote.infotify.SourceDto
import com.thecode.infotify.domain.model.Topic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * The mapper is deliberately thin — deduplication and date normalisation moved to the
 * proxy — so what is tested here is defensiveness: a client must never crash on a payload
 * it did not expect, even one from its own server.
 */
class FeedMapperTest {

    private val mapper = FeedMapper()

    @Test
    fun `maps a well-formed article`() {
        val page = mapper.toDomain(response(article()))
        val article = page.articles.single()

        assertEquals("https://example.org/story", article.url)
        assertEquals(Instant.parse("2026-09-02T05:41:00Z"), article.publishedAt)
        assertEquals("Example Times", article.source.name)
    }

    @Test
    fun `drops articles that cannot be opened or dated`() {
        val page = mapper.toDomain(
            response(
                article(url = null),
                article(url = "https://a.example", title = null),
                article(url = "https://b.example", publishedAt = "not-a-date"),
                article(url = "https://good.example")
            )
        )

        assertEquals(listOf("https://good.example"), page.articles.map { it.url })
    }

    @Test
    fun `keeps only topics the app knows about`() {
        val page = mapper.toDomain(
            response(article(categories = listOf("technology", "nonsense", "science")))
        )

        assertEquals(
            listOf(Topic.Technology, Topic.Science),
            page.articles.single().categories
        )
    }

    @Test
    fun `a missing source object does not crash the mapping`() {
        val page = mapper.toDomain(response(article(source = null)))

        assertTrue(page.articles.single().source.name.isNotBlank())
    }

    @Test
    fun `a null article list is an empty page`() {
        val page = mapper.toDomain(FeedResponse(articles = null, nextCursor = null, cachedAt = null))

        assertTrue(page.articles.isEmpty())
        assertNull(page.nextCursor)
    }

    @Test
    fun `a blank cursor is treated as no cursor`() {
        val page = mapper.toDomain(response(article(), nextCursor = "   "))

        assertNull(page.nextCursor)
    }

    private fun response(vararg articles: ArticleDto, nextCursor: String? = null) =
        FeedResponse(articles = articles.toList(), nextCursor = nextCursor, cachedAt = null)

    private fun article(
        url: String? = "https://example.org/story",
        title: String? = "A headline",
        publishedAt: String? = "2026-09-02T05:41:00Z",
        categories: List<String>? = listOf("top"),
        source: SourceDto? = SourceDto("example", "Example Times", null)
    ) = ArticleDto(
        id = "id",
        title = title,
        description = "A description",
        url = url,
        imageUrl = null,
        publishedAt = publishedAt,
        source = source,
        categories = categories
    )
}
