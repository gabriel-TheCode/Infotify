package com.thecode.infotify.domain.usecase

import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveInterests @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<Interests> = repository.interests()
}

class SaveInterests @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(interests: Interests) = repository.setInterests(interests)
}
