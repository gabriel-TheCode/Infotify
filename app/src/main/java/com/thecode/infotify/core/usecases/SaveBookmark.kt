package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.NewsRepository
import com.thecode.infotify.database.article.ArticleEntity
import javax.inject.Inject

class SaveBookmark @Inject constructor(
    private val repository: NewsRepository
) {
    fun saveBookmark(article: ArticleEntity){
        repository.saveBookmark(article)
    }
}