package io.ducket.api.domain.repository

import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

abstract class AttachmentRepository {

    abstract fun deleteAttachment(userId: String, entityId: String, attachmentId: String): Boolean

    abstract fun getAttachmentsAmount(entityId: String): Int

    abstract fun findAttachment(userId: String, entityId: String, attachmentId: String): Attachment?

    abstract fun createAttachment(userId: String, entityId: String, newFile: File)

    // abstract fun deleteAttachment(attachmentId: Int): Boolean

    protected fun findOne(attachmentId: String): Attachment? = transaction {
        AttachmentEntity.findById(attachmentId)?.toModel()
    }
}