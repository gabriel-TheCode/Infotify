package com.thecode.infotify.responses

import com.thecode.infotify.entities.Source

class SourceObjectResponse {
    var status: String? = null
    lateinit var sources: Array<Source>

}