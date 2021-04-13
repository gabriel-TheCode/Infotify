package com.thecode.infotify.core.network

import com.thecode.infotify.core.domain.Article

interface InfotifyRemoteDataSource {

    suspend fun fetchNews() : List<Article>

}