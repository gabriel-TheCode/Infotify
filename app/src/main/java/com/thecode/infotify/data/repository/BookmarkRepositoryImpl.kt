package com.thecode.infotify.data.repository

import com.thecode.infotify.data.local.bookmark.BookmarkDao
import com.thecode.infotify.data.local.bookmark.BookmarkMapper
import com.thecode.infotify.di.IoDispatcher
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao,
    private val mapper: BookmarkMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BookmarkRepository {

    override fun bookmarks(): Flow<List<Article>> =
        dao.observeAll()
            .map { entities -> entities.map(mapper::toDomain) }
            .flowOn(ioDispatcher)

    override fun bookmarkedUrls(): Flow<Set<String>> =
        dao.observeUrls()
            .map { it.toSet() }
            .flowOn(ioDispatcher)

    override suspend fun save(article: Article) = withContext(ioDispatcher) {
        dao.insert(mapper.toEntity(article, savedAt = System.currentTimeMillis()))
    }

    override suspend fun remove(url: String) = withContext(ioDispatcher) {
        dao.deleteByUrl(url)
    }
}
