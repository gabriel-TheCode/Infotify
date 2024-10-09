package com.thecode.infotify.domain.model

data class Source(
    var status: String?,
    var sources: List<SourceItem>
)

data class SourceItem(
    var id: String?,
    var name: String,
)
