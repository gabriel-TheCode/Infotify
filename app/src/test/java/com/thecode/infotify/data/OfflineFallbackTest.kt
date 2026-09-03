package com.thecode.infotify.data

import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.data.local.feed.CachedArticleEntity
import com.thecode.infotify.data.local.feed.FeedCacheDao
import com.thecode.infotify.data.local.feed.FeedCacheMapper
import com.thecode.infotify.data.remote.infotify.FeedMapper
import com.thecode.infotify.data.remote.infotify.InfotifyApi
import com.thecode.infotify.data.repository.NewsRepositoryImpl
import com.thecode.infotify.domain.model.Topic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The offline path: a news app is read on commutes, so losing the network must cost
 * freshness rather than the whole screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFallbackTest {

    private val scheduler = TestCoroutineScheduler()
    private val dao = InMemoryFeedCacheDao()

    private lateinit var server: MockWebServer
    private lateinit var repository: NewsRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InfotifyApi::class.java)

        repository = NewsRepositoryImpl(
            api = api,
            mapper = FeedMapper(),
            cacheDao = dao,
            cacheMapper = FeedCacheMapper(),
            ioDispatcher = StandardTestDispatcher(scheduler)
        )
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `a successful page is remembered`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(BODY))

        repository.latest(Topic.Top, "en")

        assertEquals(1, dao.rows.size)
    }

    /** The whole point: same request, no network, articles still on screen. */
    @Test
    fun `losing the network serves the remembered page instead of an error`() =
        runTest(scheduler) {
            server.enqueue(MockResponse().setBody(BODY))
            repository.latest(Topic.Top, "en")

            server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
            val outcome = repository.latest(Topic.Top, "en")

            assertTrue("offline should not be an error when a page is cached", outcome is Outcome.Success)
            val page = (outcome as Outcome.Success).data
            assertEquals(1, page.articles.size)
            assertTrue("the page must admit it is cached", page.isFromCache)
        }

    /** A cached cursor belongs to a finished session; paging on it would fail. */
    @Test
    fun `a remembered page offers no cursor`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(BODY))
        repository.latest(Topic.Top, "en")

        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        val page = (repository.latest(Topic.Top, "en") as Outcome.Success).data

        assertNull(page.nextCursor)
    }

    @Test
    fun `with nothing cached, offline is still an error`() = runTest(scheduler) {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        assertTrue(repository.latest(Topic.Top, "en") is Outcome.Failure)
    }

    /** Feeds must not overwrite one another, or switching topic would poison the cache. */
    @Test
    fun `each topic and language keeps its own page`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(BODY))
        repository.latest(Topic.Top, "en")
        server.enqueue(MockResponse().setBody(BODY))
        repository.latest(Topic.Science, "en")
        server.enqueue(MockResponse().setBody(BODY))
        repository.latest(Topic.Top, "fr")

        assertEquals(3, dao.rows.map { it.feedKey }.distinct().size)
    }

    /** Appending implies a live connection, so later pages are not worth storing. */
    @Test
    fun `later pages are not cached`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(BODY))
        repository.latest(Topic.Top, "en", cursor = "page-2")

        assertTrue(dao.rows.isEmpty())
    }

    @Test
    fun `search is never cached`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(BODY))
        repository.search("climat", "fr")

        assertTrue(dao.rows.isEmpty())
    }

    private companion object {
        val BODY = """
            {
              "articles": [
                {
                  "id": "a1",
                  "title": "A headline",
                  "description": "A description",
                  "url": "https://example.org/story",
                  "imageUrl": null,
                  "publishedAt": "2026-09-02T05:41:00Z",
                  "source": { "id": "example", "name": "Example Times", "iconUrl": null },
                  "categories": ["top"]
                }
              ],
              "nextCursor": "cursor-1",
              "cachedAt": null
            }
        """.trimIndent()
    }
}

/** Shared by the repository suites; the cache is behaviour, not a detail to mock away. */
internal class InMemoryFeedCacheDao : FeedCacheDao {
    val rows = mutableListOf<CachedArticleEntity>()

    override suspend fun page(feedKey: String) =
        rows.filter { it.feedKey == feedKey }.sortedBy { it.position }

    override suspend fun cachedAt(feedKey: String) =
        rows.filter { it.feedKey == feedKey }.minOfOrNull { it.cachedAt }

    override suspend fun insert(articles: List<CachedArticleEntity>) {
        rows += articles
    }

    override suspend fun clear(feedKey: String) {
        rows.removeAll { it.feedKey == feedKey }
    }

    override suspend fun clearAll() = rows.clear()
}
