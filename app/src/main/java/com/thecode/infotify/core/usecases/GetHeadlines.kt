package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.repositories.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetHeadlines @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun getHeadlines(language: String, category: String): Flow<DataState<News>> = flow {
        emit(DataState.Loading)
        try {
            val data = repository.fetchHeadlinesByLangAndCat(language, category)
            if (data.status == "ok") {
                if (data.articles.isEmpty()) {
                    emit(DataState.Error(Exception("No result found")))
                } else {
                    emit(DataState.Success(data))
                }
            } else {
                emit(DataState.Error(Exception("API error")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(Exception(e.message)))
        }
    }.flowOn(Dispatchers.IO)
}
