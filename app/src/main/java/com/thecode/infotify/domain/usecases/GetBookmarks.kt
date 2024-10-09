package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.remote.mapper.NewsMapper
import com.thecode.infotify.data.repositories.NewsRepository
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetBookmarks @Inject constructor(
    private val repository: NewsRepository,
    private val newsMapper: NewsMapper

) {
    suspend fun getBookmarks(): Flow<DataState<List<Article>>> = flow {
        emit(DataState.Loading)
        repository.getBookmarks().collect { it ->
            if (it.isEmpty()) {
                emit(DataState.Error(Exception("Data must not be empty")))
            } else {
                emit(DataState.Success(it.map { newsMapper.newsEntityToItem(it) }))
            }
        }
    }.flowOn(Dispatchers.IO)
}
