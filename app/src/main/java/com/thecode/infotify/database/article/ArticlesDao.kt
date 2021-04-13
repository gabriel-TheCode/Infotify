package com.thecode.infotify.database.article

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
public interface ArticlesDao {

    @Query("SELECT * FROM article")
    fun getAllArticles(): List<ArticleEntity?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM article")
    fun clear()

    @Query("DELETE FROM article WHERE url = :primaryId")
    fun deleteByPrimaryId(primaryId: String?): Int

}