package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.MainRepository
import javax.inject.Inject

class SetNightModeEnabled @Inject constructor(
    private val repository: MainRepository
) {
    suspend operator fun invoke(state: Boolean) {
        repository.setNightModeEnabled(state)
    }
}
