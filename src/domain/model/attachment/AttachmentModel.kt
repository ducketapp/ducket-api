package io.ducket.api.domain.model.attachment

import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.CombinedIdTable
import io.ducket.api.domain.model.StringIdTable
import io.ducket.api.domain.model.transfer.TransfersTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant
import java.util.*

internal object AttachmentsTable : StringIdTable("attachment") {
    val filePath = varchar("file_path", 128)
    val createdAt = timestamp("created_at")
}

class AttachmentEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, AttachmentEntity>(AttachmentsTable)

    var filePath by AttachmentsTable.filePath
    var createdAt by AttachmentsTable.createdAt

    fun toModel() = Attachment(id.value, filePath, createdAt)
}

data class Attachment(
    val id: String,
    val filePath: String,
    val createdAt: Instant,
)