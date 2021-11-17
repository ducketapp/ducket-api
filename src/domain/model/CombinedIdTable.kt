package io.ducket.api.domain.model

import org.jetbrains.exposed.dao.id.IdTable
import java.util.UUID.randomUUID

open class CombinedIdTable(name: String) : IdTable<String>(name) {
    open val primaryId = long("id").uniqueIndex().autoIncrement()
    open val secondaryId = varchar("uuid", 36).uniqueIndex().default(randomUUID().toString())

    override val primaryKey = PrimaryKey(primaryId)
    override val id = secondaryId.entityId()
}