package com.thecode.infotify.presentation.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.usecases.GetSearchNews
import com.thecode.infotify.core.usecases.SaveBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchNews: GetSearchNews,
    private val saveBookmark: SaveBookmark
) : ViewModel() {
    private val _searchState = MutableLiveData<DataState<News>>()
    val searchState: LiveData<DataState<News>>
        get() = _searchState

    fun getSearchNews(query: String, language: String, category: String) {
        viewModelScope.launch {
            _searchState.value.let { _ ->
                getSearchNews.getSearchNews(query, language, category).onEach {
                    when (it) {
                        is DataState.Success -> _searchState.value = it
                        else -> {}
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun saveBookmark(article: Article){
        viewModelScope.launch {
            saveBookmark.invoke(article)
        }
    }
}