package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.MainRepository
import javax.inject.Inject

class ResetAppPreferences @Inject constructor(
    private val repository: MainRepository
) {
    suspend operator fun invoke() {
        repository.clearAppData()
    }
}
