package com.thecode.infotify.domain.usecase

import com.thecode.infotify.domain.repository.CacheRepository
import javax.inject.Inject

class HttpCacheSize @Inject constructor(
    private val repository: CacheRepository
) {
    operator fun invoke(): String = repository.readableSize()
}

class ClearHttpCache @Inject constructor(
    private val repository: CacheRepository
) {
    suspend operator fun invoke() = repository.clear()
}
