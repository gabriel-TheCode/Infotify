package com.thecode.infotify.domain.repository

interface CacheRepository {

    /** Human-readable size of the on-device response cache, e.g. "4,2 Mo". */
    fun readableSize(): String

    suspend fun clear()
}
