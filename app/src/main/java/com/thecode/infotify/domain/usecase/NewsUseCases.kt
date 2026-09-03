package com.thecode.infotify.domain.usecase

import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.domain.repository.NewsRepository
import javax.inject.Inject

class GetLatestNews @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        topic: Topic,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage> = repository.latest(topic, languageCode, cursor)
}

/** The personalised feed. Five interests cost one upstream credit, same as one. */
class GetForYouNews @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        interests: Interests,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage> = repository.forYou(interests, languageCode, cursor)
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
