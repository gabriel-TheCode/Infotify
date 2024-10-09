package com.thecode.infotify.presentation.main.headline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.model.DataState
import com.thecode.infotify.domain.model.News
import com.thecode.infotify.domain.usecases.GetHeadlines
import com.thecode.infotify.domain.usecases.SaveBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun getHeadlines(category: String) {
        Log.d("Headlines", "Category : $category")
        viewModelScope.launch {
            getHeadlines.invoke(category).collect {
                _headlineState.value = it
            }
        }
    }

    fun saveBookmark(article: Article) {
        viewModelScope.launch {
            saveBookmark.invoke(article)
        }
    }
}
