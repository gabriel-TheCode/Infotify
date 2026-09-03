package com.thecode.infotify.data.local.feed

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The last feed the device successfully loaded, kept so the app has something to show
 * without a network.
 *
 * A news app is read on commutes, in lifts and on aeroplanes. Until now, no signal meant
 * an error panel and nothing else — the worst possible answer for the moment people
 * actually open a reader.
 *
 * [feedKey] identifies which feed the row belongs to, so the personalised feed and each
 * explored topic keep their own copy instead of overwriting one another.
 *
 * [position] preserves the server's ordering, which carries editorial weight: the first
 * article is the lead story, not merely the newest.
 */
@Entity(
    tableName = "cached_article",
    primaryKeys = ["feedKey", "url"],
    indices = [Index("feedKey", "position")]
)
data class CachedArticleEntity(
    val feedKey: String,
    val url: String,
    val position: Int,
    val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceId: String,
    val sourceName: String,
    val sourceIconUrl: String?,
    val categories: String,
    /** When this page was stored, so the UI can say how old what it is showing is. */
    val cachedAt: Long
)
