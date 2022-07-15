package io.ducket.api.domain.repository

import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.operation.OperationAttachmentsTable
import io.ducket.api.domain.model.user.UserEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.Instant

class OperationAttachmentRepository {

    fun findOne(operationId: Long, attachmentId: Long): Attachment? = transaction {
        val query = AttachmentsTable.select {
            AttachmentsTable.id.eq(attachmentId)
                .and {
                    exists(OperationAttachmentsTable.select {
                        OperationAttachmentsTable.attachmentId.eq(attachmentId)
                            .and(OperationAttachmentsTable.operationId.eq(operationId))
                    })
                }
        }
        AttachmentEntity.wrapRows(query).firstOrNull()?.toModel()
    }

    fun getCount(operationId: Long): Long = transaction {
        OperationAttachmentsTable.select {
            OperationAttachmentsTable.operationId.eq(operationId)
        }.count()
    }

    fun createOne(userId: Long, operationId: Long, newFile: File): Unit = transaction {
        AttachmentEntity.new {
            user = UserEntity[userId]
            filePath = newFile.absolutePath
            createdAt = Instant.now()
        }.also { attachment ->
            OperationAttachmentsTable.insert {
                it[this.attachmentId] = attachment.id.value
                it[this.operationId] = operationId
            }
        }
    }

    fun delete(operationId: Long, vararg attachmentIds: Long): Unit = transaction {
        OperationAttachmentsTable.deleteWhere {
            OperationAttachmentsTable.operationId.eq(operationId).and(OperationAttachmentsTable.attachmentId.inList(attachmentIds.toList()))
        }
    }
}