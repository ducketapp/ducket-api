package io.ducket.api.domain.model.attachment

import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object AttachmentsTable : LongIdTable("attachment") {
    val userId = reference("user_id", UsersTable)
    val filePath = varchar("file_path", 128)
    val createdAt = timestamp("created_at")
}

class AttachmentEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AttachmentEntity>(AttachmentsTable)

    var user by UserEntity referencedOn AttachmentsTable.userId
    var filePath by AttachmentsTable.filePath
    var createdAt by AttachmentsTable.createdAt

    fun toModel() = Attachment(
        id.value,
        user.toModel(),
        filePath,
        createdAt,
    )
}

data class Attachment(
    val id: Long,
    val user: User,
    val filePath: String,
    val createdAt: Instant,
)