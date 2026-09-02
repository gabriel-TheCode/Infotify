package com.thecode.infotify.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.Category
import com.thecode.infotify.domain.usecase.GetLatestNews
import com.thecode.infotify.domain.usecase.ObserveBookmarkedUrls
import com.thecode.infotify.domain.usecase.ObserveLanguage
import com.thecode.infotify.domain.usecase.ToggleBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getLatestNews: GetLatestNews,
    private val observeLanguage: ObserveLanguage,
    private val observeBookmarkedUrls: ObserveBookmarkedUrls,
    private val toggleBookmark: ToggleBookmark
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<FeedEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var nextCursor: String? = null

    /**
     * The in-flight load. Switching category cancels the previous request rather than
     * letting both complete — the old ViewModels stacked collectors, so a stale response
     * could overwrite a newer one.
     */
    private var loadJob: Job? = null

    init {
        observeBookmarks()
        load(_uiState.value.category, isRefresh = false)
    }

    fun onIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Refresh -> load(_uiState.value.category, isRefresh = true)
            FeedIntent.Retry -> load(_uiState.value.category, isRefresh = false)
            FeedIntent.LoadMore -> loadMore()
            is FeedIntent.SelectCategory -> selectCategory(intent.category)
            is FeedIntent.OpenArticle -> emit(FeedEffect.OpenReader(intent.article.url))
            is FeedIntent.ShareArticle -> emit(FeedEffect.Share(intent.article))
            is FeedIntent.ToggleBookmark -> viewModelScope.launch {
                val wasSaved = intent.article.url in _uiState.value.bookmarkedUrls
                val nowSaved = toggleBookmark(intent.article, wasSaved)
                emit(FeedEffect.ShowMessage(bookmarkAdded = nowSaved))
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            observeBookmarkedUrls().collect { urls ->
                _uiState.update { it.copy(bookmarkedUrls = urls) }
            }
        }
    }

    private fun selectCategory(category: Category) {
        if (category == _uiState.value.category) return
        _uiState.update {
            it.copy(category = category, articles = emptyList(), phase = FeedUiState.Phase.Loading)
        }
        load(category, isRefresh = false)
    }

    private fun load(category: Category, isRefresh: Boolean) {
        loadJob?.cancel()
        nextCursor = null
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshing = isRefresh,
                    phase = if (isRefresh || it.hasContent) it.phase else FeedUiState.Phase.Loading
                )
            }

            when (val outcome = getLatestNews(category, currentLanguage())) {
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
    }

    private fun loadMore() {
        val cursor = nextCursor ?: return
        val state = _uiState.value
        if (state.isAppending || state.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAppending = true) }
            when (val outcome = getLatestNews(state.category, currentLanguage(), cursor)) {
                is Outcome.Success -> {
                    nextCursor = outcome.data.nextCursor
                    _uiState.update { current ->
                        val existing = current.articles.mapTo(mutableSetOf()) { it.url }
                        current.copy(
                            articles = current.articles + outcome.data.articles
                                .filterNot { it.url in existing },
                            isAppending = false,
                            canAppend = outcome.data.nextCursor != null
                        )
                    }
                }

                is Outcome.Failure -> {
                    // An append failure must not wipe the page the user is reading:
                    // surface it as a transient message and keep the content on screen.
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

    private suspend fun currentLanguage(): String = observeLanguage().first()

    private fun emit(effect: FeedEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
