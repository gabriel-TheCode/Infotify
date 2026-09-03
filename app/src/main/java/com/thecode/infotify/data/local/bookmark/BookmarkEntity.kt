package com.thecode.infotify.data.local.bookmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A bookmarked article.
 *
 * [publishedAt] is stored as an ISO-8601 string rather than epoch millis on purpose: the
 * previous NewsAPI-era rows already held ISO-8601 text, so the 1 -> 2 migration can carry
 * them across unchanged.
 *
 * [savedAt] is epoch millis and exists so the list can be ordered by when the user saved
 * it, which is what a bookmarks screen should show — not by publication date.
 */
@Entity(tableName = "article")
data class BookmarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "imageUrl") val imageUrl: String?,
    @ColumnInfo(name = "publishedAt") val publishedAt: String,
    @ColumnInfo(name = "sourceId") val sourceId: String,
    @ColumnInfo(name = "sourceName") val sourceName: String,
    @ColumnInfo(name = "sourceIconUrl") val sourceIconUrl: String?,
    @ColumnInfo(name = "categories") val categories: String,
    @ColumnInfo(name = "savedAt") val savedAt: Long
)
