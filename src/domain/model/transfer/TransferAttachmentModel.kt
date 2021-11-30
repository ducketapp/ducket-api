package io.ducket.api.domain.model.transfer

import io.ducket.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.sql.Table

internal object TransferAttachmentsTable : Table("transfer_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable)
    val transferId = reference("transfer_id", TransfersTable)

    override val primaryKey = PrimaryKey(attachmentId, transferId)
}
