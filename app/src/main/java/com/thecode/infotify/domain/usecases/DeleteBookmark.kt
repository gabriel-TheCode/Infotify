package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.NewsRepository
import javax.inject.Inject

class DeleteBookmark @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(url: String) {
        repository.deleteBookmark(url)
    }
}
