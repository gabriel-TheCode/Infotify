package com.thecode.infotify.framework.datasource.network.mapper

interface EntityMapper<Entity, DomainModel> {

    fun mapToDomain(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity
}