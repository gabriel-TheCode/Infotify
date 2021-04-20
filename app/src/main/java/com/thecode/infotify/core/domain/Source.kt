package com.thecode.infotify.core.domain

class Source(
    var status: String? = null,
    var sources: List<SourceItem>
)

data class SourceItem(
    var id: String? = null,
    var cnbc: String? = null,
    var name: String? = null,
    var description: String? = null,
    var url: String? = null,
    var category: String? = null,
    var language: String? = null,
    var country: String? = null
)
