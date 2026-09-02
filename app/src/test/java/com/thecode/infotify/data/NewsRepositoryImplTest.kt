package com.thecode.infotify.data

import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.data.remote.newsdata.NewsDataApi
import com.thecode.infotify.data.remote.newsdata.NewsDataMapper
import com.thecode.infotify.data.repository.NewsRepositoryImpl
import com.thecode.infotify.domain.model.Category
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
            .create(NewsDataApi::class.java)

        repository = NewsRepositoryImpl(
            api = api,
            mapper = NewsDataMapper(),
            ioDispatcher = StandardTestDispatcher(scheduler)
        )
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `maps a successful response to articles`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(SUCCESS_BODY))

        val outcome = repository.latest(Category.Top, "en")

        assertTrue(outcome is Outcome.Success)
        val page = (outcome as Outcome.Success).data
        assertEquals(1, page.articles.size)
        assertEquals("cursor-1", page.nextCursor)
    }

    @Test
    fun `429 becomes QuotaExceeded, so the UI can say retrying will not help`() = runTest(scheduler) {
        server.enqueue(MockResponse().setResponseCode(429))

        assertEquals(
            Outcome.Failure(AppError.QuotaExceeded),
            repository.latest(Category.Top, "en")
        )
    }

    @Test
    fun `401 becomes InvalidCredentials`() = runTest(scheduler) {
        server.enqueue(MockResponse().setResponseCode(401))

        assertEquals(
            Outcome.Failure(AppError.InvalidCredentials),
            repository.latest(Category.Top, "en")
        )
    }

    @Test
    fun `500 keeps its status code for logging`() = runTest(scheduler) {
        server.enqueue(MockResponse().setResponseCode(500))

        assertEquals(
            Outcome.Failure(AppError.Server(500)),
            repository.latest(Category.Top, "en")
        )
    }

    @Test
    fun `a dropped connection becomes NoConnection`() = runTest(scheduler) {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        assertEquals(
            Outcome.Failure(AppError.NoConnection),
            repository.latest(Category.Top, "en")
        )
    }

    @Test
    fun `malformed json is reported as unexpected, never thrown`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody("{ this is not json"))

        val outcome = repository.latest(Category.Top, "en")

        assertTrue(outcome is Outcome.Failure)
        assertTrue((outcome as Outcome.Failure).error is AppError.Unexpected)
    }

    @Test
    fun `search sends the query and no category`() = runTest(scheduler) {
        server.enqueue(MockResponse().setBody(SUCCESS_BODY))

        repository.search("climat", "fr")

        val path = server.takeRequest().path.orEmpty()
        assertTrue(path.contains("q=climat"))
        assertTrue(path.contains("language=fr"))
        assertTrue(!path.contains("category="))
    }

    private companion object {
        val SUCCESS_BODY = """
            {
              "status": "success",
              "totalResults": 1,
              "results": [
                {
                  "article_id": "a1",
                  "link": "https://example.org/story",
                  "title": "A headline",
                  "description": "A description",
                  "pubDate": "2026-09-02 05:41:00",
                  "image_url": "https://example.org/i.jpg",
                  "category": ["top"],
                  "source_id": "example",
                  "source_name": "Example Times",
                  "source_icon": null,
                  "duplicate": false
                }
              ],
              "nextPage": "cursor-1"
            }
        """.trimIndent()
    }
}
