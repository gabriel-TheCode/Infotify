package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.LanguageRepository
import javax.inject.Inject

class SaveLanguagePreference @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(lang: String) {
        repository.setUserLanguagePreference(lang)
    }
}
