package com.thecode.infotify.domain.repository

import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Category

/**
 * The domain's contract for fetching news. Implemented in the data layer.
 *
 * This interface is what makes the dependency rule hold: use cases depend on it,
 * never on a concrete repository, and never on Retrofit or Room.
 */
interface NewsRepository {

    /**
     * Latest articles for [category], in [languageCode] (ISO 639-1).
     * [cursor] is null for the first page, otherwise the value from the previous page.
     */
    suspend fun latest(
        category: Category,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage>

    /**
     * Full-text search across the provider's index.
     */
    suspend fun search(
        query: String,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage>
}
