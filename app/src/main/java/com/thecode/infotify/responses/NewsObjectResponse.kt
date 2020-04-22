package com.thecode.infotify.responses

import com.thecode.infotify.entities.Article

class NewsObjectResponse {
    var status: String? = null
    var totalResults: String? = null
    lateinit var articles: Array<Article>


}