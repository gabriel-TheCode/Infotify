package com.thecode.infotify.data.repository

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.data.local.feed.FeedCacheDao
import com.thecode.infotify.data.local.feed.FeedCacheMapper
import com.thecode.infotify.data.remote.infotify.FeedMapper
import com.thecode.infotify.data.remote.infotify.FeedResponse
import com.thecode.infotify.data.remote.infotify.InfotifyApi
import com.thecode.infotify.di.IoDispatcher
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: InfotifyApi,
    private val mapper: FeedMapper,
    private val cacheDao: FeedCacheDao,
    private val cacheMapper: FeedCacheMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NewsRepository {

    override suspend fun latest(
        topic: Topic,
        languageCode: String,
        cursor: String?
    ): Outcome<ArticlePage> = request(
        // Only first pages are cached. A cached page 3 without pages 1 and 2 is not a
        // feed, and appending is a deliberate act that already implies a connection.
        cacheKey = cacheKey("topic", topic.apiValue, languageCode).takeIf { cursor == null }
    ) {
        api.feed(
            language = languageCode,
            categories = topic.apiValue,
            cursor = cursor
        )
    }

    override suspend fun forYou(
        interests: Interests,
        languageCode: String,
        cursor: String?
    ): Outcome<ArticlePage> = request(
        cacheKey = cacheKey(
            "foryou",
            interests.topics.map { it.apiValue }.sorted().joinToString("+") +
                (interests.region?.let { "@" + it.name } ?: ""),
            languageCode
        ).takeIf { cursor == null }
    ) {
        api.feed(
            language = languageCode,
            // No interests selected is not an error: it falls back to the editor's feed,
            // so "For you" is never a dead end for someone who skipped onboarding.
            categories = interests.topics
                .takeIf { it.isNotEmpty() }
                ?.joinToString(",") { it.apiValue }
                ?: Topic.Default.apiValue,
            country = interests.region?.query,
            cursor = cursor
        )
    }

    override suspend fun search(
        query: String,
        languageCode: String,
        cursor: String?
    ): Outcome<ArticlePage> = request {
        // Search is never cached. A remembered result for a query typed minutes ago is
        // not useful, and caching every query would fill the database with noise.
        api.feed(
            language = languageCode,
            query = query,
            cursor = cursor
        )
    }

    /**
     * Runs [block] off the main thread and turns every failure into a named [AppError].
     * Nothing above this line ever sees an [HttpException] or an [IOException].
     */
    private suspend inline fun request(
        cacheKey: String? = null,
        crossinline block: suspend () -> FeedResponse
    ): Outcome<ArticlePage> = withContext(ioDispatcher) {
        try {
            val page = mapper.toDomain(block())
            if (cacheKey != null && page.articles.isNotEmpty()) {
                cacheDao.replace(
                    cacheKey,
                    cacheMapper.toEntities(cacheKey, page.articles, System.currentTimeMillis())
                )
            }
            Outcome.Success(page)
        } catch (e: MalformedJsonException) {
            // Must precede IOException: MalformedJsonException extends it, so a corrupt
            // payload would otherwise be reported to the user as "you are offline".
            fallback(cacheKey, AppError.Unexpected(e))
        } catch (e: JsonParseException) {
            fallback(cacheKey, AppError.Unexpected(e))
        } catch (e: IOException) {
            fallback(cacheKey, AppError.NoConnection)
        } catch (e: HttpException) {
            fallback(
                cacheKey,
                when (e.code()) {
                    // The proxy forwards upstream quota exhaustion as 429, so the app can
                    // say "come back tomorrow" instead of "check your connection".
                    HTTP_TOO_MANY_REQUESTS -> AppError.QuotaExceeded
                    HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> AppError.InvalidCredentials
                    else -> AppError.Server(e.code())
                }
            )
        } catch (e: Exception) {
            fallback(cacheKey, AppError.Unexpected(e))
        }
    }

    /**
     * The stored page for [cacheKey], or the failure if there is nothing to fall back on.
     *
     * Deliberately a success, not a softened error: from the screen's point of view there
     * are articles to render. The page carries [ArticlePage.cachedAt] so the UI can say
     * plainly that it is showing something remembered rather than pretending it is live.
     *
     * nextCursor is null, because the cursor that came with the cached page belongs to a
     * session that has ended and paging on it would fail.
     */
    private suspend fun fallback(cacheKey: String?, error: AppError): Outcome<ArticlePage> {
        if (cacheKey == null) return Outcome.Failure(error)
        val cached = cacheDao.page(cacheKey)
        if (cached.isEmpty()) return Outcome.Failure(error)
        return Outcome.Success(
            ArticlePage(
                articles = cached.map(cacheMapper::toDomain),
                nextCursor = null,
                cachedAt = Instant.ofEpochMilli(cached.first().cachedAt)
            )
        )
    }

    /** Language is part of the key: the same topic in French is a different feed. */
    private fun cacheKey(kind: String, selector: String, languageCode: String) =
        "$kind|$selector|$languageCode"

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_FORBIDDEN = 403
        const val HTTP_TOO_MANY_REQUESTS = 429
    }
}
