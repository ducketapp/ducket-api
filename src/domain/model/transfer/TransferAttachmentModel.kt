package io.ducket.api.domain.model.transfer

import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.user.UserAttachmentsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import java.util.*

internal object TransferAttachmentsTable : Table("transfer_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable)
    val transferId = reference("transfer_id", TransfersTable)

    override val primaryKey = PrimaryKey(attachmentId, transferId)
}

/*
class TransferAttachmentEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<TransferAttachmentEntity>(TransferAttachmentsTable)

    var attachment by AttachmentEntity referencedOn TransferAttachmentsTable.attachmentId
    var transfer by TransferEntity referencedOn TransferAttachmentsTable.transferId

    fun toModel() = TransferAttachment(id.value, attachment.toModel(), transfer.toModel())
}

data class TransferAttachment(
    val id: UUID,
    val attachment: Attachment,
    val transfer: Transfer,
)*/
