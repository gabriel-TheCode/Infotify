package com.thecode.infotify.data.local.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FeedCacheDao {

    @Query("SELECT * FROM cached_article WHERE feedKey = :feedKey ORDER BY position ASC")
    suspend fun page(feedKey: String): List<CachedArticleEntity>

    @Query("SELECT MIN(cachedAt) FROM cached_article WHERE feedKey = :feedKey")
    suspend fun cachedAt(feedKey: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: List<CachedArticleEntity>)

    @Query("DELETE FROM cached_article WHERE feedKey = :feedKey")
    suspend fun clear(feedKey: String)

    /**
     * Replaces a feed atomically. Without the transaction, a process death between the
     * delete and the insert would leave the user with an empty cache and no network —
     * strictly worse than the stale page they had.
     */
    @Transaction
    suspend fun replace(feedKey: String, articles: List<CachedArticleEntity>) {
        clear(feedKey)
        insert(articles)
    }

    @Query("DELETE FROM cached_article")
    suspend fun clearAll()
}
