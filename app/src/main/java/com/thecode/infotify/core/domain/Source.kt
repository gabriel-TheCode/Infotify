package com.thecode.infotify.core.domain

data class Source(
    var status: String?,
    var sources: List<SourceItem>
)

data class SourceItem(
    var id: String?,
    var name: String,
)
