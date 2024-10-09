package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.NewsRepository
import com.thecode.infotify.domain.model.DataState
import com.thecode.infotify.domain.model.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetHeadlines @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(category: String): Flow<DataState<News>> = flow {
        emit(DataState.Loading)
        try {
            val data = repository.fetchHeadlinesByCategory(category)
            if (data.status == "ok") {
                if (data.articles.isEmpty()) {
                    emit(DataState.Error(Exception("No result found")))
                } else {
                    emit(DataState.Success(data))
                }
            } else {
                emit(DataState.Error(Exception("API error: ${data.status}")))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
