package com.thecode.infotify.presentation.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.domain.usecase.ObserveInterests
import com.thecode.infotify.domain.usecase.SaveInterests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Editing interests after onboarding.
 *
 * Unlike onboarding, changes are committed on every tap: there is no "save" button, so
 * there is nothing to forget to press, and the feed behind updates as soon as the user
 * goes back.
 */
@HiltViewModel
class InterestsViewModel @Inject constructor(
    observeInterests: ObserveInterests,
    private val saveInterests: SaveInterests
) : ViewModel() {

    val uiState = observeInterests().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Interests.None
    )

    fun onToggleTopic(topic: Topic) {
        viewModelScope.launch { saveInterests(uiState.value.toggle(topic)) }
    }

    fun onToggleRegion(region: Region) {
        viewModelScope.launch { saveInterests(uiState.value.toggle(region)) }
    }
}
