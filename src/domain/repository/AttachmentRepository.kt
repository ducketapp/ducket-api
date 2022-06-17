package io.ducket.api.domain.repository

import io.ducket.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.sql.transactions.transaction

class AttachmentRepository {

    fun findAllPaths(): List<String> = transaction {
        AttachmentEntity.all().map { it.filePath }
    }
}