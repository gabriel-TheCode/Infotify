package com.thecode.infotify.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.domain.usecase.GetForYouNews
import com.thecode.infotify.domain.usecase.GetLatestNews
import com.thecode.infotify.domain.usecase.ObserveBookmarkedUrls
import com.thecode.infotify.domain.usecase.ObserveInterests
import com.thecode.infotify.domain.usecase.ObserveLanguage
import com.thecode.infotify.domain.usecase.ToggleBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getLatestNews: GetLatestNews,
    private val getForYouNews: GetForYouNews,
    private val observeLanguage: ObserveLanguage,
    observeInterests: ObserveInterests,
    private val observeBookmarkedUrls: ObserveBookmarkedUrls,
    private val toggleBookmark: ToggleBookmark
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<FeedEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var nextCursor: String? = null

    /** What the current feed is made of. Any change to it reloads. */
    private data class FeedQuery(
        val mode: FeedMode,
        val topic: Topic,
        val interests: Interests,
        val language: String,
        val refreshToken: Int
    )

    private val refreshTrigger = MutableStateFlow(0)
    private val mode = MutableStateFlow(FeedMode.ForYou)
    private val topic = MutableStateFlow(Topic.Default)

    init {
        observeBookmarks()

        // The whole feed is derived from its inputs. Previously the language was read once
        // with .first() at request time and never observed, so changing it in Settings did
        // not reload the feed. flatMapLatest also cancels the in-flight request whenever an
        // input changes, so a stale response can never overwrite a newer one.
        viewModelScope.launch {
            combine(
                mode,
                topic,
                observeInterests(),
                observeLanguage(),
                refreshTrigger
            ) { mode, topic, interests, language, token ->
                FeedQuery(mode, topic, interests, language, token)
            }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        _uiState.update {
                            it.copy(
                                mode = query.mode,
                                topic = query.topic,
                                interests = query.interests,
                                discoveryTopic = discoveryTopic(query.interests),
                                phase = if (it.hasContent) it.phase else FeedUiState.Phase.Loading
                            )
                        }
                        emit(load(query))
                    }
                }
                .collect(::applyResult)
        }
    }

    fun onIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Refresh -> {
                _uiState.update { it.copy(isRefreshing = true) }
                refreshTrigger.update { it + 1 }
            }

            FeedIntent.Retry -> refreshTrigger.update { it + 1 }
            FeedIntent.LoadMore -> loadMore()
            FeedIntent.OpenInterests -> emit(FeedEffect.NavigateToInterests)

            is FeedIntent.SelectMode -> if (mode.value != intent.mode) {
                _uiState.update { it.copy(articles = emptyList()) }
                mode.value = intent.mode
            }

            is FeedIntent.SelectTopic -> if (topic.value != intent.topic) {
                _uiState.update { it.copy(articles = emptyList()) }
                topic.value = intent.topic
            }

            is FeedIntent.OpenArticle -> emit(FeedEffect.OpenReader(intent.article.url))
            is FeedIntent.ShareArticle -> emit(FeedEffect.Share(intent.article))
            is FeedIntent.ToggleBookmark -> viewModelScope.launch {
                val wasSaved = intent.article.url in _uiState.value.bookmarkedUrls
                val nowSaved = toggleBookmark(intent.article, wasSaved)
                emit(FeedEffect.ShowMessage(bookmarkAdded = nowSaved))
            }
        }
    }

    private suspend fun load(query: FeedQuery): Outcome<ArticlePage> {
        nextCursor = null
        return when (query.mode) {
            FeedMode.ForYou -> getForYouNews(query.interests, query.language)
            FeedMode.Explore -> getLatestNews(query.topic, query.language)
        }
    }

    private fun applyResult(outcome: Outcome<ArticlePage>) {
        when (outcome) {
            is Outcome.Success -> {
                nextCursor = outcome.data.nextCursor
                _uiState.update {
                    it.copy(
                        articles = outcome.data.articles,
                        phase = if (outcome.data.articles.isEmpty()) {
                            FeedUiState.Phase.Empty
                        } else {
                            FeedUiState.Phase.Content
                        },
                        isRefreshing = false,
                        canAppend = outcome.data.nextCursor != null,
                        error = null
                    )
                }
            }

            is Outcome.Failure -> onFailure(outcome.error)
        }
    }

    private fun loadMore() {
        val cursor = nextCursor ?: return
        val state = _uiState.value
        if (state.isAppending || state.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAppending = true) }
            val language = currentLanguage()
            val outcome = when (state.mode) {
                FeedMode.ForYou -> getForYouNews(state.interests, language, cursor)
                FeedMode.Explore -> getLatestNews(state.topic, language, cursor)
            }

            when (outcome) {
                is Outcome.Success -> {
                    nextCursor = outcome.data.nextCursor
                    _uiState.update { current ->
                        val existing = current.articles.mapTo(mutableSetOf()) { it.url }
                        current.copy(
                            articles = current.articles +
                                outcome.data.articles.filterNot { it.url in existing },
                            isAppending = false,
                            canAppend = outcome.data.nextCursor != null
                        )
                    }
                }

                is Outcome.Failure -> {
                    _uiState.update { it.copy(isAppending = false, canAppend = false) }
                    emit(FeedEffect.ShowError(outcome.error))
                }
            }
        }
    }

    /**
     * A failure with content already on screen is a transient message, not a state change:
     * wiping a page the user is reading because a refresh failed is worse than the failure.
     */
    private fun onFailure(error: AppError) {
        val hasContent = _uiState.value.hasContent
        _uiState.update {
            it.copy(
                isRefreshing = false,
                phase = if (hasContent) FeedUiState.Phase.Content else FeedUiState.Phase.Error,
                error = error
            )
        }
        if (hasContent) emit(FeedEffect.ShowError(error))
    }

    /**
     * One unselected subject, stable for the day so it does not flicker on every reload.
     * This is what keeps the personalised feed from hardening into a bubble — the invitation
     * to look elsewhere sits inside the feed itself, and never nags.
     */
    private fun discoveryTopic(interests: Interests): Topic? {
        if (interests.topics.isEmpty()) return null
        val candidates = Topic.entries.filterNot { it in interests.topics || it == Topic.Top }
        if (candidates.isEmpty()) return null
        val seed = LocalDate.now().toEpochDay()
        return candidates[Random(seed).nextInt(candidates.size)]
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            observeBookmarkedUrls().collect { urls ->
                _uiState.update { it.copy(bookmarkedUrls = urls) }
            }
        }
    }

    private suspend fun currentLanguage(): String = observeLanguage().first()

    private fun emit(effect: FeedEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
