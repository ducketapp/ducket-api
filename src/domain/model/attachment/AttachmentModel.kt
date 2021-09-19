package io.budgery.api.domain.model.attachment

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object AttachmentsTable : IntIdTable("attachment") {
    val filePath = varchar("file_path", 128)
    val originalFileName = varchar("original_file_name", 45)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class AttachmentEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AttachmentEntity>(AttachmentsTable)

    var filePath by AttachmentsTable.filePath
    var originalFileName by AttachmentsTable.originalFileName
    var createdAt by AttachmentsTable.createdAt
    var modifiedAt by AttachmentsTable.modifiedAt

    fun toModel() = Attachment(id.value, filePath, originalFileName, createdAt, modifiedAt)
}

data class Attachment(
    val id: Int,
    val filePath: String,
    val originalFileName: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)