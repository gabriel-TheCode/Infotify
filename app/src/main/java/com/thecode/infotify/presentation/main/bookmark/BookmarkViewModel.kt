package com.thecode.infotify.presentation.main.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.usecases.DeleteBookmark
import com.thecode.infotify.core.usecases.GetBookmarks
import com.thecode.infotify.core.usecases.SaveBookmark
import com.thecode.infotify.database.article.ArticleEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    private val getBookmarks: GetBookmarks,
    private val deleteBookmark: DeleteBookmark
) : ViewModel() {
    private val _articles = MutableLiveData<DataState<List<ArticleEntity>>>()
    val articles: LiveData<DataState<List<ArticleEntity>>>
        get() = _articles

    fun getBookmarks(){
        viewModelScope.launch {
            _articles.value.let { _ ->
                getBookmarks.getBookmarks().onEach {
                    when (it) {
                        is DataState.Success -> _articles.value = it
                        is DataState.Error -> TODO()
                        DataState.Loading -> TODO()
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun deleteBookmark(url: String) {
        viewModelScope.launch {
            deleteBookmark.invoke(url)
        }
    }
}