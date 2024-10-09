package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsNightModeEnabled @Inject constructor(
    private val repository: MainRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isNightModeEnabled()
    }
}
