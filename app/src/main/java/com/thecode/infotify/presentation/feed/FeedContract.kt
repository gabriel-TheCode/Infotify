package com.thecode.infotify.presentation.feed

import androidx.compose.runtime.Immutable
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.Category

/**
 * Feed screen contract.
 *
 * [phase] separates Loading, Content, Empty and Error as four distinct states. The old
 * build had only success/loading/error, so an empty-but-valid response was reported to the
 * user as a connectivity failure.
 */
@Immutable
data class FeedUiState(
    val category: Category = Category.Default,
    val articles: List<Article> = emptyList(),
    val bookmarkedUrls: Set<String> = emptySet(),
    val phase: Phase = Phase.Loading,
    val isRefreshing: Boolean = false,
    val isAppending: Boolean = false,
    val canAppend: Boolean = false,
    /** Set only alongside [Phase.Error]; the panel needs the cause to word its message. */
    val error: AppError? = null
) {
    enum class Phase { Loading, Content, Empty, Error }

    val hasContent: Boolean get() = articles.isNotEmpty()
}

sealed interface FeedIntent {
    data object Refresh : FeedIntent
    data object Retry : FeedIntent
    data object LoadMore : FeedIntent
    data class SelectCategory(val category: Category) : FeedIntent
    data class OpenArticle(val article: Article) : FeedIntent
    data class ToggleBookmark(val article: Article) : FeedIntent
    data class ShareArticle(val article: Article) : FeedIntent
}

/** Consumed once. Never persisted in [FeedUiState] — that is the whole distinction. */
sealed interface FeedEffect {
    data class OpenReader(val url: String) : FeedEffect
    data class Share(val article: Article) : FeedEffect
    data class ShowMessage(val bookmarkAdded: Boolean) : FeedEffect
    data class ShowError(val error: AppError) : FeedEffect
}
