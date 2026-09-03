package com.thecode.infotify.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.usecase.ObserveBookmarks
import com.thecode.infotify.domain.usecase.RemoveBookmark
import com.thecode.infotify.domain.usecase.SaveBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    observeBookmarks: ObserveBookmarks,
    private val removeBookmark: RemoveBookmark,
    private val saveBookmark: SaveBookmark
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<BookmarksEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        // Collected once. Room re-emits on every change, so there is nothing to refresh:
        // the old screen re-subscribed on every pull-to-refresh and stacked collectors.
        viewModelScope.launch {
            observeBookmarks().collect { articles ->
                _uiState.value = BookmarksUiState(
                    articles = articles,
                    phase = if (articles.isEmpty()) {
                        BookmarksUiState.Phase.Empty
                    } else {
                        BookmarksUiState.Phase.Content
                    }
                )
            }
        }
    }

    fun onIntent(intent: BookmarksIntent) {
        when (intent) {
            is BookmarksIntent.OpenArticle -> emit(BookmarksEffect.OpenReader(intent.article.url))
            is BookmarksIntent.ShareArticle -> emit(BookmarksEffect.Share(intent.article))

            is BookmarksIntent.Remove -> viewModelScope.launch {
                removeBookmark(intent.article.url)
                emit(BookmarksEffect.Removed(intent.article))
            }

            is BookmarksIntent.UndoRemove -> viewModelScope.launch {
                saveBookmark(intent.article)
            }
        }
    }

    private fun emit(effect: BookmarksEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
