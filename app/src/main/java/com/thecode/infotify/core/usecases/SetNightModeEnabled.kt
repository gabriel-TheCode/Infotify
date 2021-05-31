package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.MainRepository
import javax.inject.Inject

class SetNightModeEnabled @Inject constructor(
    private val repository: MainRepository
) {
    operator fun invoke(state: Boolean) {
        repository.setNightModeEnabled(state)
    }
}