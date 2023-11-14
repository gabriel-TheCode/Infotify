package com.thecode.infotify.core.domain

sealed class OnBoardingState {
    data class COMPLET(val list: List<OnBoardingPart>) : OnBoardingState()
}
