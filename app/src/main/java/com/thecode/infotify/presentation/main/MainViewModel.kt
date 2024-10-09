package com.thecode.infotify.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.usecases.IsNightModeEnabled
import com.thecode.infotify.domain.usecases.SaveBookmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled,
    private val saveBookmark: SaveBookmark
) : ViewModel() {

    private val _isNightModeState = MutableLiveData<Boolean>()

    fun isNightModeActivated(): Boolean {
        viewModelScope.launch {
            isNightModeEnabled.invoke().collect {
                _isNightModeState.value = it
            }
        }

        return _isNightModeState.value ?: false
    }

    fun saveBookmark(article: Article) {
        viewModelScope.launch { saveBookmark.invoke(article) }
    }
}
