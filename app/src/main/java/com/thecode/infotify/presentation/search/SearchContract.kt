package com.thecode.infotify.presentation.search

import androidx.compose.runtime.Immutable
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.domain.model.Article

@Immutable
data class SearchUiState(
    val query: String = "",
    val articles: List<Article> = emptyList(),
    val bookmarkedUrls: Set<String> = emptySet(),
    val phase: Phase = Phase.Idle,
    val isAppending: Boolean = false,
    val canAppend: Boolean = false,
    val error: AppError? = null
) {
    /** Idle is the state before any query — distinct from a query that found nothing. */
    enum class Phase { Idle, Loading, Content, Empty, Error }
}

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object Submit : SearchIntent
    data object Clear : SearchIntent
    data object Retry : SearchIntent
    data object LoadMore : SearchIntent
    data class OpenArticle(val article: Article) : SearchIntent
    data class ToggleBookmark(val article: Article) : SearchIntent
    data class ShareArticle(val article: Article) : SearchIntent
}

sealed interface SearchEffect {
    data class OpenReader(val url: String) : SearchEffect
    data class Share(val article: Article) : SearchEffect
    data class ShowMessage(val bookmarkAdded: Boolean) : SearchEffect
    data class ShowError(val error: AppError) : SearchEffect
}
