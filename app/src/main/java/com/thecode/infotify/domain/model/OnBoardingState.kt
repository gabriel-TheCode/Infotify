package com.thecode.infotify.domain.model

sealed class OnBoardingState {
    data class Complete(val list: List<OnBoardingPart>) : OnBoardingState()
}
