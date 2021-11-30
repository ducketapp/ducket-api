package io.ducket.api.domain.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.util.*

open class StringIdTable(name: String = "", columnName: String = "id", columnLength: Int = 36) : IdTable<String>(name) {
    override val id: Column<EntityID<String>> = varchar(columnName, columnLength)
        .clientDefault { UUID.randomUUID().toString() }
        .uniqueIndex()
        .entityId()

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}