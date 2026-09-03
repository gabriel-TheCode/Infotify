package com.thecode.infotify.data.local

import android.content.ContentValues
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.time.Instant

/**
 * Bookmarks are the only user-created data in the app, so this migration copies every
 * existing row rather than dropping the table.
 *
 * The v1 schema stored the publisher as a Gson blob in a single `source` column. Extracting
 * it in SQL would need the JSON1 extension, which is not guaranteed on every API 24 device,
 * so the rows are read out and re-parsed in Kotlin instead.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        val legacyRows = readLegacyRows(db)

        db.execSQL("DROP TABLE IF EXISTS `source`")
        db.execSQL("ALTER TABLE `article` RENAME TO `article_old`")
        db.execSQL(
            """
            CREATE TABLE `article` (
                `url` TEXT NOT NULL,
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT,
                `imageUrl` TEXT,
                `publishedAt` TEXT NOT NULL,
                `sourceId` TEXT NOT NULL,
                `sourceName` TEXT NOT NULL,
                `sourceIconUrl` TEXT,
                `categories` TEXT NOT NULL,
                `savedAt` INTEGER NOT NULL,
                PRIMARY KEY(`url`)
            )
            """.trimIndent()
        )

        val now = System.currentTimeMillis()
        legacyRows.forEachIndexed { index, row ->
            db.insert(
                "article",
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put("url", row.url)
                    put("id", row.url)
                    put("title", row.title)
                    put("description", row.description)
                    put("imageUrl", row.imageUrl)
                    put("publishedAt", row.publishedAt ?: Instant.EPOCH.toString())
                    put("sourceId", row.sourceId)
                    put("sourceName", row.sourceName)
                    putNull("sourceIconUrl")
                    put("categories", "")
                    // Preserve the original ordering; older rows get older timestamps.
                    put("savedAt", now - (legacyRows.size - index))
                }
            )
        }

        db.execSQL("DROP TABLE `article_old`")
    }

    private fun readLegacyRows(db: SupportSQLiteDatabase): List<LegacyBookmark> {
        val gson = Gson()
        val rows = mutableListOf<LegacyBookmark>()
        db.query(
            "SELECT `url`, `title`, `description`, `urlToImage`, `publishedAt`, `source` FROM `article`"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val url = cursor.getString(0) ?: continue
                val source = parseLegacySource(gson, cursor.getString(5))
                rows += LegacyBookmark(
                    url = url,
                    title = cursor.getString(1) ?: url,
                    description = cursor.getString(2),
                    imageUrl = cursor.getString(3),
                    publishedAt = cursor.getString(4),
                    sourceId = source?.id.orEmpty(),
                    sourceName = source?.name.orEmpty()
                )
            }
        }
        return rows
    }

    private fun parseLegacySource(gson: Gson, raw: String?): LegacySource? {
        if (raw.isNullOrBlank()) return null
        return try {
            gson.fromJson(raw, LegacySource::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }
}

private data class LegacyBookmark(
    val url: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val publishedAt: String?,
    val sourceId: String,
    val sourceName: String
)

private data class LegacySource(val id: String?, val name: String?)
