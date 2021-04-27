package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.NewsRepository
import com.thecode.infotify.database.article.ArticleEntity
import javax.inject.Inject

class GetBookmarks @Inject constructor(
    private val repository: NewsRepository
) {
    fun getBookmarks(): List<ArticleEntity> {
        return repository.getBookmarks()
    }
}