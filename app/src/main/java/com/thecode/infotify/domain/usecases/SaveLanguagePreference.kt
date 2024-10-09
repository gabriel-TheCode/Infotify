package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.LanguageRepository
import javax.inject.Inject

class SaveLanguagePreference @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(lang: String) {
        repository.setUserLanguagePreference(lang)
    }
}
