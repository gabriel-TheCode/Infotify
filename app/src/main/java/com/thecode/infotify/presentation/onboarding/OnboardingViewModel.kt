package com.thecode.infotify.presentation.onboarding

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.domain.usecase.CompleteOnboarding
import com.thecode.infotify.domain.usecase.SaveInterests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class OnboardingUiState(
    val step: Step = Step.Welcome,
    val interests: Interests = Interests.None
) {
    enum class Step { Welcome, Interests, Notifications }

    val isLastStep: Boolean get() = step == Step.Notifications
}

/**
 * Onboarding holds a draft of the interests and commits once, at the end.
 *
 * Saving on every tap would have been simpler, but it would also mean that abandoning
 * onboarding halfway leaves half a preference behind.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveInterests: SaveInterests,
    private val completeOnboarding: CompleteOnboarding
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun onToggleTopic(topic: Topic) {
        _uiState.update { it.copy(interests = it.interests.toggle(topic)) }
    }

    fun onToggleRegion(region: Region) {
        _uiState.update { it.copy(interests = it.interests.toggle(region)) }
    }

    fun onNext() {
        _uiState.update { state ->
            val next = when (state.step) {
                OnboardingUiState.Step.Welcome -> OnboardingUiState.Step.Interests
                OnboardingUiState.Step.Interests -> OnboardingUiState.Step.Notifications
                OnboardingUiState.Step.Notifications -> OnboardingUiState.Step.Notifications
            }
            state.copy(step = next)
        }
    }

    fun onBack(): Boolean {
        val state = _uiState.value
        val previous = when (state.step) {
            OnboardingUiState.Step.Welcome -> return false
            OnboardingUiState.Step.Interests -> OnboardingUiState.Step.Welcome
            OnboardingUiState.Step.Notifications -> OnboardingUiState.Step.Interests
        }
        _uiState.update { it.copy(step = previous) }
        return true
    }

    /**
     * Commits whatever the user chose — including nothing at all. An empty selection is a
     * valid answer: the app falls back to the editor's feed and invites personalisation
     * later, rather than blocking the way in.
     */
    fun onFinish(onDone: () -> Unit) {
        viewModelScope.launch {
            saveInterests(_uiState.value.interests)
            completeOnboarding()
            onDone()
        }
    }
}
