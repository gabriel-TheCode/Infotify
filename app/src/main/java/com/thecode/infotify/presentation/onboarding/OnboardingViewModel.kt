package com.thecode.infotify.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.core.domain.OnBoardingState
import com.thecode.infotify.core.usecases.GetOnBoardingParts
import com.thecode.infotify.core.usecases.SetOnboardingCompleted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setOnboardingCompleted: SetOnboardingCompleted,
    private val getOnBoardingParts: GetOnBoardingParts
) : ViewModel() {


    private val _state = MutableLiveData<OnBoardingState>()
    val state: LiveData<OnBoardingState>
        get() = _state

    fun getOnBoardingSlide() {
        viewModelScope.launch {
            val list = getOnBoardingParts()
            _state.value = OnBoardingState.COMPLET(list)
        }
    }


    fun setOnboardingCompleted() {
        setOnboardingCompleted.invoke()
    }
}