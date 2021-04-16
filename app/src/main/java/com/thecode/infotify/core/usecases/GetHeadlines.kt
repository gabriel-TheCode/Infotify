package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.data.NewsRepository
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.domain.News
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetHeadlines @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun getHeadlines(language: String, category: String): Flow<DataState<News>> = flow {
        emit(DataState.Loading)
        val data = repository.fetchHeadlinesByLangAndCat(language, category)
        if (data.articles.isEmpty()) {
            emit(DataState.Error(Exception("Data must not be empty")))
        } else {
            emit(DataState.Success(data))
        }
        emit(DataState.Success(data))
    }
}