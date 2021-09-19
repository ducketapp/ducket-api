package io.budgery.api.domain.model.transfer

import io.budgery.api.domain.model.attachment.Attachment
import io.budgery.api.domain.model.attachment.AttachmentEntity
import io.budgery.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object TransferAttachmentsTable : IntIdTable("transfer_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable)
    val transferId = reference("transfer_id", TransfersTable)
}

class TransferAttachmentEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransferAttachmentEntity>(TransferAttachmentsTable)

    var attachment by AttachmentEntity referencedOn TransferAttachmentsTable.attachmentId
    var transfer by TransferEntity referencedOn TransferAttachmentsTable.transferId

    fun toModel() = TransferAttachment(id.value, attachment.toModel(), transfer.toModel())
}

data class TransferAttachment(
    val id: Int,
    val attachment: Attachment,
    val transfer: Transfer,
)