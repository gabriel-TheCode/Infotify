package com.thecode.infotify.domain.usecase

import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThemeMode @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode()
}

class SetThemeMode @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}

class ObserveLanguage @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<String> = repository.languageCode()
}

class SetLanguage @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(code: String) = repository.setLanguageCode(code)
}

class IsOnboardingCompleted @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isOnboardingCompleted()
}

class CompleteOnboarding @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke() = repository.setOnboardingCompleted()
}
