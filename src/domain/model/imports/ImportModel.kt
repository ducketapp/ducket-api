package dev.ducket.api.domain.model.imports

import dev.ducket.api.domain.model.user.User
import dev.ducket.api.domain.model.user.UserEntity
import dev.ducket.api.domain.model.user.UsersTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object ImportsTable : LongIdTable("import") {
    val userId = reference("user_id", UsersTable)
    val title = varchar("title", 64)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class ImportEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ImportEntity>(ImportsTable)

    var user by UserEntity referencedOn ImportsTable.userId
    var title by ImportsTable.title
    var createdAt by ImportsTable.createdAt
    var modifiedAt by ImportsTable.modifiedAt

    fun toModel() = Import(
        id.value,
        user.toModel(),
        title,
        createdAt,
        modifiedAt,
    )
}

data class Import(
    val id: Long,
    val user: User,
    val title: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class ImportCreate(
    val userId: Long,
    val title: String,
)

data class ImportUpdate(
    val title: String,
)
