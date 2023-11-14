package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.repositories.NewsRepository
import javax.inject.Inject

class SaveBookmark @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun invoke(article: Article) {
        repository.saveBookmark(article)
    }
}
