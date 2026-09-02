package com.thecode.infotify.domain.usecase

import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Category
import com.thecode.infotify.domain.repository.NewsRepository
import javax.inject.Inject

class GetLatestNews @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        category: Category,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage> = repository.latest(category, languageCode, cursor)
}

class SearchNews @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        query: String,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage> = repository.search(query, languageCode, cursor)
}
