package com.thecode.infotify.presentation.main.headline

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.domain.DataState
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.core.usecases.GetHeadlines
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HeadlineViewModel @ViewModelInject constructor(
    private val getHeadlines: GetHeadlines
) : ViewModel() {
    private val _headlineState = MutableLiveData<DataState<News>>()
    val headlineState: LiveData<DataState<News>>
        get() = _headlineState

    fun getHeadlines(language: String, category: String) {
        viewModelScope.launch {
            _headlineState.value.let { _ ->
                getHeadlines.getHeadlines(language, category).onEach {
                    when (it) {
                        is DataState.Success -> _headlineState.value = it
                        is DataState.Error -> TODO()
                        DataState.Loading -> TODO()
                    }
                }.launchIn(viewModelScope)
            }
        }
    }
}