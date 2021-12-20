package io.ducket.api.domain.model.attachment

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

internal object AttachmentsTable : LongIdTable("attachment") {
    val filePath = varchar("file_path", 128)
    val createdAt = timestamp("created_at")
}

class AttachmentEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AttachmentEntity>(AttachmentsTable)

    var filePath by AttachmentsTable.filePath
    var createdAt by AttachmentsTable.createdAt

    fun toModel() = Attachment(
        id.value,
        filePath,
        createdAt,
    )
}

data class Attachment(
    val id: Long,
    val filePath: String,
    val createdAt: Instant,
)