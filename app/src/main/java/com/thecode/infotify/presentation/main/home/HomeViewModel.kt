package com.thecode.infotify.presentation.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.usecases.IsNightModeEnabled
import com.thecode.infotify.core.usecases.SetNightModeEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled,
    private val setNightModeEnabled: SetNightModeEnabled
) : ViewModel() {

    private val _isNightModeState = MutableLiveData<Boolean>()
    val isNightModeState: LiveData<Boolean>
        get() = _isNightModeState

    fun setNightMode(state: Boolean) {
        viewModelScope.launch { setNightModeEnabled(state) }
    }

    fun fetchNightMode() {
        viewModelScope.launch {
            isNightModeEnabled.invoke().collect {
                _isNightModeState.value = it
            }
        }
    }
}
