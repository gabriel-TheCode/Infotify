package com.thecode.infotify.database.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SourcesDao {

    @Query("select * from source")
    fun getAllArticles(): Flow<List<SourceEntity>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllArticles(articles: List<SourceEntity>)

    @Query("DELETE FROM article")
    fun clear()

    @Query("delete from source where id = :primaryId")
    fun deleteByPrimaryId(primaryId: String?): Int
}