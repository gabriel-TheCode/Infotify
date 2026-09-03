package com.thecode.infotify.domain.usecase

import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBookmarks @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.bookmarks()
}

class ObserveBookmarkedUrls @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<Set<String>> = repository.bookmarkedUrls()
}

/**
 * Saves the article, or removes it when it is already saved.
 * Returns true when the article ended up saved, so the caller can word its confirmation.
 */
class ToggleBookmark @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(article: Article, isCurrentlySaved: Boolean): Boolean {
        if (isCurrentlySaved) {
            repository.remove(article.url)
        } else {
            repository.save(article)
        }
        return !isCurrentlySaved
    }
}

class RemoveBookmark @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(url: String) = repository.remove(url)
}

class SaveBookmark @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(article: Article) = repository.save(article)
}
