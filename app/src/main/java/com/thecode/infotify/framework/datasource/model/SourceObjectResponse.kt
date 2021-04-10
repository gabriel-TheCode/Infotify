package com.thecode.infotify.framework.datasource.model

import com.thecode.infotify.entities.Source

class SourceObjectResponse {
    var status: String? = null
    lateinit var sources: Array<Source>

}