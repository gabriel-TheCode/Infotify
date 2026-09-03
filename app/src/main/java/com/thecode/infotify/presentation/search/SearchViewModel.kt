package com.thecode.infotify.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.usecase.ObserveBookmarkedUrls
import com.thecode.infotify.domain.usecase.ObserveLanguage
import com.thecode.infotify.domain.usecase.SearchNews
import com.thecode.infotify.domain.usecase.ToggleBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNews: SearchNews,
    private val observeLanguage: ObserveLanguage,
    private val observeBookmarkedUrls: ObserveBookmarkedUrls,
    private val toggleBookmark: ToggleBookmark
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<SearchEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    /** Drives the search. flatMapLatest cancels the previous query's request. */
    private val queries = MutableStateFlow("")

    private var nextCursor: String? = null

    init {
        observeBookmarks()
        viewModelScope.launch {
            queries
                .debounce(DEBOUNCE_MILLIS)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        if (query.length < MIN_QUERY_LENGTH) {
                            emit(null)
                        } else {
                            _uiState.update { it.copy(phase = SearchUiState.Phase.Loading) }
                            emit(searchNews(query, currentLanguage()))
                        }
                    }
                }
                .collect { outcome -> applyResult(outcome) }
        }
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _uiState.update { it.copy(query = intent.query) }
                queries.value = intent.query.trim()
            }

            SearchIntent.Submit -> queries.value = _uiState.value.query.trim()

            SearchIntent.Clear -> {
                _uiState.value = SearchUiState()
                queries.value = ""
                nextCursor = null
            }

            SearchIntent.Retry -> viewModelScope.launch {
                val query = _uiState.value.query.trim()
                if (query.length >= MIN_QUERY_LENGTH) {
                    _uiState.update { it.copy(phase = SearchUiState.Phase.Loading) }
                    applyResult(searchNews(query, currentLanguage()))
                }
            }

            SearchIntent.LoadMore -> loadMore()

            is SearchIntent.OpenArticle -> emit(SearchEffect.OpenReader(intent.article.url))
            is SearchIntent.ShareArticle -> emit(SearchEffect.Share(intent.article))
            is SearchIntent.ToggleBookmark -> viewModelScope.launch {
                val wasSaved = intent.article.url in _uiState.value.bookmarkedUrls
                val nowSaved = toggleBookmark(intent.article, wasSaved)
                emit(SearchEffect.ShowMessage(bookmarkAdded = nowSaved))
            }
        }
    }

    private fun applyResult(outcome: Outcome<com.thecode.infotify.domain.model.ArticlePage>?) {
        when (outcome) {
            null -> {
                nextCursor = null
                _uiState.update {
                    it.copy(
                        articles = emptyList(),
                        phase = SearchUiState.Phase.Idle,
                        canAppend = false,
                        error = null
                    )
                }
            }

            is Outcome.Success -> {
                nextCursor = outcome.data.nextCursor
                _uiState.update {
                    it.copy(
                        articles = outcome.data.articles,
                        phase = if (outcome.data.articles.isEmpty()) {
                            SearchUiState.Phase.Empty
                        } else {
                            SearchUiState.Phase.Content
                        },
                        canAppend = outcome.data.nextCursor != null,
                        error = null
                    )
                }
            }

            is Outcome.Failure -> _uiState.update {
                it.copy(phase = SearchUiState.Phase.Error, error = outcome.error)
            }
        }
    }

    private fun loadMore() {
        val cursor = nextCursor ?: return
        val state = _uiState.value
        if (state.isAppending) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAppending = true) }
            when (val outcome = searchNews(state.query.trim(), currentLanguage(), cursor)) {
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
                    emit(SearchEffect.ShowError(outcome.error))
                }
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

    private suspend fun currentLanguage(): String = observeLanguage().first()

    private fun emit(effect: SearchEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 400L
        const val MIN_QUERY_LENGTH = 2
    }
}
