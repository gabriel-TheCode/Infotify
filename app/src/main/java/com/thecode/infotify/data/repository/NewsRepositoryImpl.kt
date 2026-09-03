package com.thecode.infotify.data.repository

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
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
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: InfotifyApi,
    private val mapper: FeedMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NewsRepository {

    override suspend fun latest(
        topic: Topic,
        languageCode: String,
        cursor: String?
    ): Outcome<ArticlePage> = request {
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
    ): Outcome<ArticlePage> = request {
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
        crossinline block: suspend () -> FeedResponse
    ): Outcome<ArticlePage> = withContext(ioDispatcher) {
        try {
            Outcome.Success(mapper.toDomain(block()))
        } catch (e: MalformedJsonException) {
            // Must precede IOException: MalformedJsonException extends it, so a corrupt
            // payload would otherwise be reported to the user as "you are offline".
            Outcome.Failure(AppError.Unexpected(e))
        } catch (e: JsonParseException) {
            Outcome.Failure(AppError.Unexpected(e))
        } catch (e: IOException) {
            Outcome.Failure(AppError.NoConnection)
        } catch (e: HttpException) {
            Outcome.Failure(
                when (e.code()) {
                    // The proxy forwards upstream quota exhaustion as 429, so the app can
                    // say "come back tomorrow" instead of "check your connection".
                    HTTP_TOO_MANY_REQUESTS -> AppError.QuotaExceeded
                    HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> AppError.InvalidCredentials
                    else -> AppError.Server(e.code())
                }
            )
        } catch (e: Exception) {
            Outcome.Failure(AppError.Unexpected(e))
        }
    }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_FORBIDDEN = 403
        const val HTTP_TOO_MANY_REQUESTS = 429
    }
}
