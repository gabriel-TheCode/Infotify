package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.LanguageRepository
import javax.inject.Inject

class GetLanguages @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    operator fun invoke(): List<String> {
        return languageRepository.getLanguages().map { it.label }
    }
}
