package com.thecode.infotify.presentation.main.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.usecases.DeleteBookmark
import com.thecode.infotify.core.usecases.GetBookmarks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarks: GetBookmarks,
    private val deleteBookmark: DeleteBookmark
) : ViewModel() {
    private val _articles = MutableLiveData<DataState<List<Article>>>()
    val articles: LiveData<DataState<List<Article>>>
        get() = _articles

    fun getBookmarks() {
        viewModelScope.launch {
            _articles.value.let { _ ->
                getBookmarks.getBookmarks().onEach {
                    when (it) {
                        is DataState.Success -> _articles.value = it
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