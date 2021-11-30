package io.ducket.api.domain.model.user

import domain.model.user.UsersTable
import io.ducket.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.sql.Table

internal object UserAttachmentsTable : Table("user_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable)
    val userId = reference("user_id", UsersTable)

    override val primaryKey = PrimaryKey(attachmentId, userId)
}
