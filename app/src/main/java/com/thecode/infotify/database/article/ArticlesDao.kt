package com.thecode.infotify.database.article

import androidx.room.*

@Dao
interface ArticlesDao {

    @Query("select * from article where url = :primaryId")
    fun findByPrimaryId(primaryId: String?): ArticleEntity?

    @Query("DELETE FROM article WHERE url = :primaryId")
    fun deleteByPrimaryId(primaryId: String?): Int

    @Query("SELECT * FROM article")
    fun getAllArticles(): List<ArticleEntity>

    @Query("DELETE FROM article")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articleEntity: ArticleEntity?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg articleEntities: ArticleEntity?): LongArray?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(articleEntityList: List<ArticleEntity?>?): LongArray?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(articleEntity: ArticleEntity?): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg articleEntities: ArticleEntity?): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(articleEntityList: List<ArticleEntity?>?): Int

    @Delete
    fun delete(articleEntity: ArticleEntity?): Int
}