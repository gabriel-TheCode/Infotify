package com.thecode.infotify.presentation.main.headline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.usecases.GetHeadlines
import com.thecode.infotify.core.usecases.SaveBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeadlineViewModel @Inject constructor(
    private val getHeadlines: GetHeadlines,
    private val saveBookmark: SaveBookmark
) : ViewModel() {
    private val _headlineState = MutableLiveData<DataState<News>>()
    val headlineState: LiveData<DataState<News>>
        get() = _headlineState

    fun getHeadlines(language: String, category: String) {

        viewModelScope.launch {
            _headlineState.value.let { _ ->
                getHeadlines.getHeadlines(language, category).onEach {
                    _headlineState.value = it
                }.launchIn(viewModelScope)
            }
        }
    }

    fun saveBookmark(article: Article) {
        viewModelScope.launch {
            saveBookmark.invoke(article)
        }
    }
}