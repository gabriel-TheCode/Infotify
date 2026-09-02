package com.thecode.infotify.presentation.bookmarks

import androidx.compose.runtime.Immutable
import com.thecode.infotify.domain.model.Article

@Immutable
data class BookmarksUiState(
    val articles: List<Article> = emptyList(),
    val phase: Phase = Phase.Loading
) {
    /**
     * No Error phase: the source is a local Room Flow, which does not fail the way a
     * network call does. An empty table is Empty, not an error — the previous build
     * emitted DataState.Error("Data must not be empty") for exactly this case.
     */
    enum class Phase { Loading, Content, Empty }
}

sealed interface BookmarksIntent {
    data class OpenArticle(val article: Article) : BookmarksIntent
    data class Remove(val article: Article) : BookmarksIntent
    data class UndoRemove(val article: Article) : BookmarksIntent
    data class ShareArticle(val article: Article) : BookmarksIntent
}

sealed interface BookmarksEffect {
    data class OpenReader(val url: String) : BookmarksEffect
    data class Share(val article: Article) : BookmarksEffect

    /** Carries the removed article so the snackbar's Undo can put it back. */
    data class Removed(val article: Article) : BookmarksEffect
}
