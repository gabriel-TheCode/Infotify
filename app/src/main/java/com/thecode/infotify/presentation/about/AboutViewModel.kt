package com.thecode.infotify.presentation.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.usecases.IsNightModeEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled
) : ViewModel() {

    private val _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean>
        get() = _state

    fun isNightModeActivated(): Boolean {
        viewModelScope.launch {
            _state.value.let { _ ->
                isNightModeEnabled.invoke().collect {
                    _state.value = it
                }
            }
        }

        return _state.value == true
    }
}
