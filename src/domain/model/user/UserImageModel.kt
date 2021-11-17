package io.ducket.api.domain.model.user

import domain.model.user.UsersTable
import io.ducket.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table

internal object UserAttachmentsTable : Table("user_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable)
    val userId = reference("user_id", UsersTable)

    override val primaryKey = PrimaryKey(attachmentId, userId)
}

/*
class UserAttachmentEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserAttachmentEntity>(UserAttachmentsTable)

    var attachment by AttachmentEntity referencedOn UserAttachmentsTable.attachmentId
    var user by UserEntity referencedOn UserAttachmentsTable.userId

    fun toModel() = UserAttachment(attachment.toModel(), user.toModel())
}

data class UserAttachment(
    val attachment: Attachment,
    val user: User,
)*/
