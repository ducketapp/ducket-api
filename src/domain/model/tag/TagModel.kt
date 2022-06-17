package io.ducket.api.domain.model.tag

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object TagsTable : LongIdTable("tag") {
    val userId = reference("user_id", UsersTable)
    val name = varchar("name", 32)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class TagEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TagEntity>(TagsTable)

    var user by UserEntity referencedOn TagsTable.userId
    var name by TagsTable.name
    var createdAt by TagsTable.createdAt
    var modifiedAt by TagsTable.modifiedAt

    fun toModel() = Tag(
        id.value,
        user.toModel(),
        name,
        createdAt,
        modifiedAt,
    )
}

data class Tag(
    val id: Long,
    val user: User,
    val name: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)