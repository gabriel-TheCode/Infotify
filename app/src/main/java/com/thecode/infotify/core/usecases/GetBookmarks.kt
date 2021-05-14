package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.repositories.NewsRepository
import com.thecode.infotify.database.article.ArticleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBookmarks @Inject constructor(
    private val repository: NewsRepository,

) {
    fun getBookmarks(): Flow<DataState<List<ArticleEntity>>> = flow  {
        emit(DataState.Loading)
        repository.getBookmarks().collect {
            if (it.isEmpty()) {
                emit(DataState.Error(Exception("Data must not be empty")))
            } else {
                emit(DataState.Success(it))
            }
        }
    }
}