package com.thecode.infotify.presentation.main.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.usecases.DeleteBookmark
import com.thecode.infotify.core.usecases.GetBookmarks
import com.thecode.infotify.core.usecases.SaveBookmark
import com.thecode.infotify.database.article.ArticleEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    private val getBookmarks: GetBookmarks,
    private val saveBookmark: SaveBookmark,
    private val deleteBookmark: DeleteBookmark
) : ViewModel() {

    private val _articles = MutableLiveData<List<ArticleEntity>>()
    val articles: LiveData<List<ArticleEntity>>
        get() = _articles

    fun getBookmarks(): List<ArticleEntity>  {
        val articles = getBookmarks()
        _articles.value = articles
        return articles
    }

    fun saveBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            saveBookmark(article)
        }
    }

    fun deleteBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            deleteBookmark(article)
        }
    }
}