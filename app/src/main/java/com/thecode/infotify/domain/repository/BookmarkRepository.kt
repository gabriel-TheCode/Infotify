package com.thecode.infotify.domain.repository

import com.thecode.infotify.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    /** Emits the saved articles, newest first, and re-emits on every change. */
    fun bookmarks(): Flow<List<Article>>

    /** Emits the set of bookmarked article URLs, for marking cards in any list. */
    fun bookmarkedUrls(): Flow<Set<String>>

    suspend fun save(article: Article)

    suspend fun remove(url: String)
}
