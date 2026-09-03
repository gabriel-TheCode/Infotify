package com.thecode.infotify.data.repository

import android.content.Context
import com.thecode.infotify.di.IoDispatcher
import com.thecode.infotify.domain.repository.CacheRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CacheRepository {

    private val cacheDir: File get() = context.cacheDir.resolve("http_cache")

    override fun readableSize(): String {
        val bytes = cacheDir.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.0f kB", bytes / 1024.0)
            else -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        cacheDir.deleteRecursively()
        Unit
    }
}
