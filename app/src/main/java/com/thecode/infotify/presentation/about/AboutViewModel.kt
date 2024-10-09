package com.thecode.infotify.presentation.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thecode.infotify.domain.usecases.IsNightModeEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled
) : ViewModel() {

    private val _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean>
        get() = _state

    fun isNightModeActivated(): Boolean {
        runBlocking {
            _state.value = isNightModeEnabled.invoke().first()
        }

        return _state.value ?: false
    }
}
