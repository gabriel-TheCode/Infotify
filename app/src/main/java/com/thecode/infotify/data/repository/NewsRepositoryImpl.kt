package com.thecode.infotify.data.repository

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.data.remote.newsdata.NewsDataApi
import com.thecode.infotify.data.remote.newsdata.NewsDataMapper
import com.thecode.infotify.di.IoDispatcher
import com.thecode.infotify.domain.model.ArticlePage
import com.thecode.infotify.domain.model.Category
import com.thecode.infotify.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsDataApi,
    private val mapper: NewsDataMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NewsRepository {

    override suspend fun latest(
        category: Category,
        languageCode: String,
        cursor: String?
    ): Outcome<ArticlePage> = request {
        api.latest(
            language = languageCode,
            category = category.apiValue,
            cursor = cursor
        )
    }

    override suspend fun search(
        query: String,
        languageCode: String,
        cursor: String?
    ): Outcome<ArticlePage> = request {
        api.latest(
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
        crossinline block: suspend () -> com.thecode.infotify.data.remote.newsdata.NewsDataResponse
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
