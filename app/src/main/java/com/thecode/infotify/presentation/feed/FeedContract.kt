package com.thecode.infotify.presentation.feed

import androidx.compose.runtime.Immutable
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Topic

/**
 * The home screen shows two feeds through one contract.
 *
 * [FeedMode.ForYou] answers "what matters to me today"; [FeedMode.Explore] keeps every
 * subject one tap away. Discovery is half the screen, not an option buried in settings —
 * that is the structural answer to filter bubbles, rather than an algorithmic one.
 */
enum class FeedMode { ForYou, Explore }

@Immutable
data class FeedUiState(
    val mode: FeedMode = FeedMode.ForYou,
    val topic: Topic = Topic.Default,
    val interests: Interests = Interests.None,
    val articles: List<Article> = emptyList(),
    val bookmarkedUrls: Set<String> = emptySet(),
    val phase: Phase = Phase.Loading,
    val isRefreshing: Boolean = false,
    val isAppending: Boolean = false,
    val canAppend: Boolean = false,
    /** A subject the user has NOT chosen, offered once per day at the end of For You. */
    val discoveryTopic: Topic? = null,
    /** Set only alongside [Phase.Error]; the panel needs the cause to word its message. */
    val error: AppError? = null,
    /**
     * Non-null when the articles on screen came from the device rather than the network.
     * The feed still renders normally — it simply says so.
     */
    val cachedAt: java.time.Instant? = null
) {
    enum class Phase { Loading, Content, Empty, Error }

    val hasContent: Boolean get() = articles.isNotEmpty()

    /** Drives the non-blocking banner inviting the user to personalise the feed. */
    val showsInterestsPrompt: Boolean
        get() = mode == FeedMode.ForYou && interests.isEmpty
}

sealed interface FeedIntent {
    data object Refresh : FeedIntent
    data object Retry : FeedIntent
    data object LoadMore : FeedIntent
    data class SelectMode(val mode: FeedMode) : FeedIntent
    data class SelectTopic(val topic: Topic) : FeedIntent
    data object OpenInterests : FeedIntent
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
    data object NavigateToInterests : FeedEffect
}
