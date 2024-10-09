package com.thecode.infotify.data.remote.mapper

interface EntityMapper<Entity, DomainModel> {

    fun mapToDomain(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity
}
