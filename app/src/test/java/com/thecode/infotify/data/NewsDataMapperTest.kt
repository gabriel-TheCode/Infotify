package com.thecode.infotify.data

import com.thecode.infotify.data.remote.newsdata.NewsDataArticle
import com.thecode.infotify.data.remote.newsdata.NewsDataMapper
import com.thecode.infotify.data.remote.newsdata.NewsDataResponse
import com.thecode.infotify.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * The mapper is the app's only defence against the provider's wire format, so its edge
 * cases are tested rather than assumed.
 */
class NewsDataMapperTest {

    private val mapper = NewsDataMapper()

    @Test
    fun `parses the provider's non-ISO date as UTC`() {
        val page = mapper.toDomain(responseOf(article(pubDate = "2026-09-02 05:41:00")))

        assertEquals(Instant.parse("2026-09-02T05:41:00Z"), page.articles.single().publishedAt)
    }

    @Test
    fun `drops articles the app could not open or render`() {
        val page = mapper.toDomain(
            responseOf(
                article(link = null),
                article(link = "https://a.example", title = null),
                article(link = "https://b.example", pubDate = "not a date"),
                article(link = "https://good.example")
            )
        )

        assertEquals(listOf("https://good.example"), page.articles.map { it.url })
    }

    @Test
    fun `drops articles the provider flagged as duplicates`() {
        val page = mapper.toDomain(
            responseOf(
                article(link = "https://a.example", duplicate = true),
                article(link = "https://b.example", duplicate = false)
            )
        )

        assertEquals(listOf("https://b.example"), page.articles.map { it.url })
    }

    @Test
    fun `collapses articles repeated under the same url`() {
        val page = mapper.toDomain(
            responseOf(
                article(link = "https://same.example"),
                article(link = "https://same.example")
            )
        )

        assertEquals(1, page.articles.size)
    }

    @Test
    fun `keeps only categories the app knows about`() {
        val page = mapper.toDomain(
            responseOf(article(category = listOf("technology", "tourism", "science")))
        )

        assertEquals(
            listOf(Category.Technology, Category.Science),
            page.articles.single().categories
        )
    }

    @Test
    fun `falls back to a placeholder when the publisher has no name`() {
        val page = mapper.toDomain(responseOf(article(sourceName = "  ")))

        assertTrue(page.articles.single().source.name.isNotBlank())
    }

    @Test
    fun `blank image and description become null rather than empty strings`() {
        val page = mapper.toDomain(responseOf(article(imageUrl = "", description = "")))

        assertNull(page.articles.single().imageUrl)
        assertNull(page.articles.single().description)
    }

    @Test
    fun `carries the pagination cursor through`() {
        val page = mapper.toDomain(responseOf(article(), nextPage = "cursor-42"))

        assertEquals("cursor-42", page.nextCursor)
    }

    @Test
    fun `a null results array is an empty page, not a crash`() {
        val page = mapper.toDomain(
            NewsDataResponse(status = "success", totalResults = 0, results = null, nextPage = null)
        )

        assertTrue(page.articles.isEmpty())
        assertNull(page.nextCursor)
    }

    private fun responseOf(vararg articles: NewsDataArticle, nextPage: String? = null) =
        NewsDataResponse(
            status = "success",
            totalResults = articles.size,
            results = articles.toList(),
            nextPage = nextPage
        )

    private fun article(
        link: String? = "https://example.org/story",
        title: String? = "A headline",
        description: String? = "A description",
        pubDate: String? = "2026-09-02 05:41:00",
        imageUrl: String? = "https://example.org/image.jpg",
        category: List<String>? = listOf("top"),
        sourceName: String? = "Example Times",
        duplicate: Boolean? = false
    ) = NewsDataArticle(
        articleId = "id",
        link = link,
        title = title,
        description = description,
        pubDate = pubDate,
        imageUrl = imageUrl,
        category = category,
        sourceId = "example",
        sourceName = sourceName,
        sourceIcon = null,
        duplicate = duplicate
    )
}
