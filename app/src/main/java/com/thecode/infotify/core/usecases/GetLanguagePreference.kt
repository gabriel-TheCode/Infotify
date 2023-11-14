package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.LanguageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguagePreference @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    operator fun invoke(): Flow<String> {
        return languageRepository.getUserLanguagePreference()
    }
}
