package com.thecode.infotify.presentation

import app.cash.turbine.test
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.ArticleSource
import com.thecode.infotify.domain.model.Category
import com.thecode.infotify.domain.repository.BookmarkRepository
import com.thecode.infotify.domain.repository.NewsRepository
import com.thecode.infotify.domain.repository.PreferencesRepository
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.usecase.GetLatestNews
import com.thecode.infotify.domain.usecase.ObserveBookmarkedUrls
import com.thecode.infotify.domain.usecase.ObserveLanguage
import com.thecode.infotify.domain.usecase.ToggleBookmark
import com.thecode.infotify.presentation.feed.FeedEffect
import com.thecode.infotify.presentation.feed.FeedIntent
import com.thecode.infotify.presentation.feed.FeedUiState
import com.thecode.infotify.presentation.feed.FeedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val news = FakeNewsRepository()
    private val bookmarks = FakeBookmarkRepository()
    private val preferences = FakePreferencesRepository()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts in Loading and settles on Content`() = runTest {
        news.result = Outcome.Success(ArticlePage(listOf(article("a")), nextCursor = null))

        val viewModel = createViewModel()
        assertEquals(FeedUiState.Phase.Loading, viewModel.uiState.value.phase)

        advanceUntilIdle()
        assertEquals(FeedUiState.Phase.Content, viewModel.uiState.value.phase)
        assertEquals(1, viewModel.uiState.value.articles.size)
    }

    /** The regression that motivated the whole state rework. */
    @Test
    fun `an empty but valid response is Empty, never Error`() = runTest {
        news.result = Outcome.Success(ArticlePage(emptyList(), nextCursor = null))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(FeedUiState.Phase.Empty, viewModel.uiState.value.phase)
    }

    @Test
    fun `a failure with no content shows the Error phase and its cause`() = runTest {
        news.result = Outcome.Failure(AppError.NoConnection)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(FeedUiState.Phase.Error, viewModel.uiState.value.phase)
        assertEquals(AppError.NoConnection, viewModel.uiState.value.error)
    }

    @Test
    fun `a failed refresh keeps the articles already on screen`() = runTest {
        news.result = Outcome.Success(ArticlePage(listOf(article("a")), nextCursor = null))
        val viewModel = createViewModel()
        advanceUntilIdle()

        news.result = Outcome.Failure(AppError.NoConnection)
        viewModel.onIntent(FeedIntent.Refresh)
        advanceUntilIdle()

        assertEquals(FeedUiState.Phase.Content, viewModel.uiState.value.phase)
        assertEquals(1, viewModel.uiState.value.articles.size)
    }

    @Test
    fun `selecting a category reloads and clears the previous articles`() = runTest {
        news.result = Outcome.Success(ArticlePage(listOf(article("a")), nextCursor = null))
        val viewModel = createViewModel()
        advanceUntilIdle()

        news.result = Outcome.Success(ArticlePage(listOf(article("b")), nextCursor = null))
        viewModel.onIntent(FeedIntent.SelectCategory(Category.Science))
        advanceUntilIdle()

        assertEquals(Category.Science, viewModel.uiState.value.category)
        assertEquals(listOf("https://b"), viewModel.uiState.value.articles.map { it.url })
    }

    @Test
    fun `appending a page keeps existing articles and drops repeats`() = runTest {
        news.result = Outcome.Success(ArticlePage(listOf(article("a")), nextCursor = "c1"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        news.result = Outcome.Success(
            ArticlePage(listOf(article("a"), article("b")), nextCursor = null)
        )
        viewModel.onIntent(FeedIntent.LoadMore)
        advanceUntilIdle()

        assertEquals(
            listOf("https://a", "https://b"),
            viewModel.uiState.value.articles.map { it.url }
        )
        assertTrue(!viewModel.uiState.value.canAppend)
    }

    @Test
    fun `opening an article emits a reader effect rather than changing state`() = runTest {
        news.result = Outcome.Success(ArticlePage(listOf(article("a")), nextCursor = null))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onIntent(FeedIntent.OpenArticle(article("a")))
            assertEquals(FeedEffect.OpenReader("https://a"), awaitItem())
        }
    }

    @Test
    fun `toggling a bookmark saves it and reports that it was added`() = runTest {
        news.result = Outcome.Success(ArticlePage(listOf(article("a")), nextCursor = null))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onIntent(FeedIntent.ToggleBookmark(article("a")))
            advanceUntilIdle()
            assertEquals(FeedEffect.ShowMessage(bookmarkAdded = true), awaitItem())
        }
        assertTrue("https://a" in bookmarks.saved)
    }

    private fun createViewModel() = FeedViewModel(
        getLatestNews = GetLatestNews(news),
        observeLanguage = ObserveLanguage(preferences),
        observeBookmarkedUrls = ObserveBookmarkedUrls(bookmarks),
        toggleBookmark = ToggleBookmark(bookmarks)
    )

    private fun article(id: String) = Article(
        id = id,
        title = "Headline $id",
        description = null,
        url = "https://$id",
        imageUrl = null,
        publishedAt = Instant.parse("2026-09-02T05:41:00Z"),
        source = ArticleSource(id = "s", name = "Source", iconUrl = null),
        categories = emptyList()
    )
}

private class FakeNewsRepository : NewsRepository {
    var result: Outcome<ArticlePage> = Outcome.Success(ArticlePage(emptyList(), null))

    override suspend fun latest(category: Category, languageCode: String, cursor: String?) = result

    override suspend fun search(query: String, languageCode: String, cursor: String?) = result
}

private class FakeBookmarkRepository : BookmarkRepository {
    val saved = mutableSetOf<String>()

    override fun bookmarks(): Flow<List<Article>> = flowOf(emptyList())

    override fun bookmarkedUrls(): Flow<Set<String>> = flowOf(emptySet())

    override suspend fun save(article: Article) {
        saved += article.url
    }

    override suspend fun remove(url: String) {
        saved -= url
    }
}

private class FakePreferencesRepository : PreferencesRepository {
    override fun themeMode(): Flow<ThemeMode> = flowOf(ThemeMode.System)
    override suspend fun setThemeMode(mode: ThemeMode) = Unit
    override fun languageCode(): Flow<String> = flowOf("en")
    override suspend fun setLanguageCode(code: String) = Unit
    override fun isOnboardingCompleted(): Flow<Boolean> = flowOf(true)
    override suspend fun setOnboardingCompleted() = Unit
}
