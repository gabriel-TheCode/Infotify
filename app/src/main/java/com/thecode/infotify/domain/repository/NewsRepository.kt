package com.thecode.infotify.domain.repository

import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Topic

/**
 * The domain's contract for fetching news. Implemented in the data layer.
 *
 * This interface is what makes the dependency rule hold: use cases depend on it, never on
 * a concrete repository, and never on Retrofit or Room.
 */
interface NewsRepository {

    /** Latest articles for a single [topic] — the Explore tab. */
    suspend fun latest(
        topic: Topic,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage>

    /**
     * The personalised feed: every selected subject and region in **one** request.
     *
     * This is why the feature is affordable — five interests cost the same single upstream
     * credit as one, because the provider accepts them in a single query.
     */
    suspend fun forYou(
        interests: Interests,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage>

    suspend fun search(
        query: String,
        languageCode: String,
        cursor: String? = null
    ): Outcome<ArticlePage>
}
