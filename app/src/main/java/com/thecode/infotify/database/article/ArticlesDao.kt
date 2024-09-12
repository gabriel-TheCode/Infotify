package com.thecode.infotify.database.article

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticlesDao {

    @Query("select * from article where url = :primaryId")
    fun findByPrimaryId(primaryId: String): ArticleEntity?

    @Query("DELETE FROM article WHERE url = :primaryId")
    suspend fun deleteByPrimaryId(primaryId: String): Int

    @Query("SELECT * FROM article")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("DELETE FROM article")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articleEntity: ArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg articleEntities: ArticleEntity): LongArray?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articleEntityList: List<ArticleEntity>): LongArray?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(articleEntity: ArticleEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg articleEntities: ArticleEntity): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(articleEntityList: List<ArticleEntity>): Int

    @Delete
    fun delete(articleEntity: ArticleEntity): Int
}
