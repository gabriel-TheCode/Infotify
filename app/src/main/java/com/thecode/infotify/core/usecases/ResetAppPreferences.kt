package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.data.MainRepository
import javax.inject.Inject

class ResetAppPreferences @Inject constructor(
    private val repository: MainRepository
) {
    operator fun invoke() {
        repository.clearAppData()
    }
}
