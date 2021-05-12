package com.thecode.infotify.presentation.main

import androidx.lifecycle.ViewModel
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.usecases.IsNightModeEnabled
import com.thecode.infotify.core.usecases.SaveBookmark
import com.thecode.infotify.database.article.ArticleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled,
    private val saveBookmark: SaveBookmark
) : ViewModel() {

    fun isNightModeActivated(): Boolean{
        return isNightModeEnabled()
    }

    fun saveBookmark(article: Article){
        saveBookmark.invoke(article)
    }
}