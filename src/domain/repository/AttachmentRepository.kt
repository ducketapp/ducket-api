package io.ducket.api.domain.repository

import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

abstract class AttachmentRepository {

    abstract fun deleteAttachment(userId: Long, entityId: Long, attachmentId: Long): Boolean

    abstract fun getAttachmentsAmount(entityId: Long): Int

    abstract fun findAttachment(userId: Long, entityId: Long, attachmentId: Long): Attachment?

    abstract fun createAttachment(userId: Long, entityId: Long, newFile: File)

    // abstract fun deleteAttachment(attachmentId: Int): Boolean

    protected fun findOne(attachmentId: Long): Attachment? = transaction {
        AttachmentEntity.findById(attachmentId)?.toModel()
    }
}