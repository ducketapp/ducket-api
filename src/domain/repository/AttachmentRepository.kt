package io.budgery.api.domain.repository

import io.budgery.api.domain.model.attachment.Attachment
import io.budgery.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

abstract class AttachmentRepository {

    abstract fun getAttachmentsAmount(entityId: Int): Int

    abstract fun findAttachment(entityId: Int, attachmentId: Int): Attachment?

    abstract fun createAttachment(entityId: Int, newFile: File, oldFileName: String)

    protected fun findOne(attachmentId: Int): Attachment? = transaction {
        AttachmentEntity.findById(attachmentId)?.toModel()
    }
}