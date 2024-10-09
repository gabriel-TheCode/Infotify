package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.NewsRepository
import com.thecode.infotify.domain.model.Article
import javax.inject.Inject

class SaveBookmark @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun invoke(article: Article) {
        repository.saveBookmark(article)
    }
}
