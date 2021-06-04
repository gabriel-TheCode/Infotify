package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsNightModeEnabled @Inject constructor(
    private val repository: MainRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isNightModeEnabled()
    }
}
