package com.thecode.infotify.core.usecases

import androidx.lifecycle.LiveData
import com.thecode.infotify.core.repositories.NewsRepository
import com.thecode.infotify.database.article.ArticleEntity
import javax.inject.Inject

class GetBookmarks @Inject constructor(
    private val repository: NewsRepository
) {
    fun getBookmarks(): LiveData<List<ArticleEntity>> {
        return repository.getBookmarks()
    }
}