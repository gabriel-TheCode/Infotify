package com.thecode.infotify.data

import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.data.local.feed.FeedCacheMapper
import com.thecode.infotify.data.remote.infotify.FeedMapper
import com.thecode.infotify.data.remote.infotify.InfotifyApi
import com.thecode.infotify.data.repository.NewsRepositoryImpl
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Verifies the boundary this repository exists to hold: no HttpException or IOException
 * ever escapes it, and every failure arrives upstream as a named [AppError].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryImplTest {

    /** Shared with runTest so the repository and the test advance on the same clock. */
    private val scheduler = TestCoroutineScheduler()

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
            cacheDao = InMemoryFeedCacheDao(),
            cacheMapper = FeedCacheMapper(),
            ioDispatcher = StandardTestDispatcher(scheduler)
        )
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `maps a successful response to articles`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(SUCCESS_BODY))

        val outcome = repository.latest(Topic.Top, "en")

        assertTrue(outcome is Outcome.Success)
        val page = (outcome as Outcome.Success).data
        assertEquals(1, page.articles.size)
        assertEquals("cursor-1", page.nextCursor)
    }

    /**
     * The economic heart of the feature: five interests must travel in ONE request. If this
     * ever became one request per interest, the shared daily quota would be gone in minutes.
     */
    @Test
    fun `for you sends every interest in a single request`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(SUCCESS_BODY))

        repository.forYou(
            interests = Interests(
                topics = setOf(Topic.Technology, Topic.Science, Topic.Health),
                region = Region.Africa
            ),
            languageCode = "fr"
        )

        assertEquals(1, server.requestCount)
        val path = server.takeRequest().path.orEmpty()
        assertTrue(path.contains("technology"))
        assertTrue(path.contains("science"))
        assertTrue(path.contains("health"))
        assertTrue(path.contains("country=ng"))
        assertTrue(path.contains("language=fr"))
    }

    @Test
    fun `for you with no interests falls back to the editor feed`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(SUCCESS_BODY))

        repository.forYou(Interests.None, "en")

        val path = server.takeRequest().path.orEmpty()
        assertTrue(path.contains("categories=top"))
    }

    @Test
    fun `429 becomes QuotaExceeded, so the UI can say retrying will not help`() =
        runTest(scheduler) {
            server.enqueue(MockResponse().setResponseCode(429))

            assertEquals(
                Outcome.Failure(AppError.QuotaExceeded),
                repository.latest(Topic.Top, "en")
            )
        }

    @Test
    fun `500 keeps its status code for logging`() = runTest(scheduler) {
        server.enqueue(MockResponse().setResponseCode(500))

        assertEquals(
            Outcome.Failure(AppError.Server(500)),
            repository.latest(Topic.Top, "en")
        )
    }

    @Test
    fun `a dropped connection becomes NoConnection`() = runTest(scheduler) {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        assertEquals(
            Outcome.Failure(AppError.NoConnection),
            repository.latest(Topic.Top, "en")
        )
    }

    @Test
    fun `malformed json is reported as unexpected, never as offline`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody("{ this is not json"))

        val outcome = repository.latest(Topic.Top, "en")

        assertTrue(outcome is Outcome.Failure)
        assertTrue((outcome as Outcome.Failure).error is AppError.Unexpected)
    }

    @Test
    fun `search sends the query and no categories`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(SUCCESS_BODY))

        repository.search("climat", "fr")

        val path = server.takeRequest().path.orEmpty()
        assertTrue(path.contains("q=climat"))
        assertTrue(!path.contains("categories="))
    }

    private companion object {
        val SUCCESS_BODY = """
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
              "cachedAt": "2026-09-02T06:00:00Z"
            }
        """.trimIndent()
    }
}
