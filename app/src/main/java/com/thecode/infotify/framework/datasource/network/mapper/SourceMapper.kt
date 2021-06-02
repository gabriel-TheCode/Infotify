package com.thecode.infotify.framework.datasource.network.mapper

import com.thecode.infotify.core.domain.Source
import com.thecode.infotify.core.domain.SourceItem
import com.thecode.infotify.framework.datasource.network.model.SourceObjectResponse
import javax.inject.Inject

class SourceMapper @Inject constructor() :
    EntityMapper<SourceObjectResponse, Source> {
    override fun mapToDomain(entity: SourceObjectResponse): Source {
        return Source(
            entity.status,
            entity.sources.map {
                mapFromSourceItems(it.source)
            })
    }

    private fun mapFromSourceItems(source: SourceObjectResponse.Result.Source): SourceItem {
        return SourceItem(
            source.id,
            source.name.toString()
        )
    }

    override fun mapToEntity(domainModel: Source): SourceObjectResponse {
        TODO("Not yet implemented")
    }
}