package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.NewsRepository
import com.thecode.infotify.domain.model.DataState
import com.thecode.infotify.domain.model.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSearchNews @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun getSearchNews(
        query: String,
        language: String,
        sortBy: String
    ): Flow<DataState<News>> = flow {
        emit(DataState.Loading)
        try {
            val data = repository.fetchNews(query, language, sortBy)
            if (data.articles.isEmpty()) {
                emit(DataState.Error(Exception("No result found")))
            } else {
                emit(DataState.Success(data))
            }
        } catch (e: Exception) {
            emit(DataState.Error(Exception(e.message)))
        }
    }.flowOn(Dispatchers.IO)
}
