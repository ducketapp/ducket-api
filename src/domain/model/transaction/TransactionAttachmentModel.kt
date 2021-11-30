package io.ducket.api.domain.model.transaction

import domain.model.transaction.TransactionsTable
import io.ducket.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.sql.Table

internal object TransactionAttachmentsTable : Table("transaction_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable.id)
    val transactionId = reference("transaction_id", TransactionsTable.id)

    override val primaryKey = PrimaryKey(attachmentId, transactionId)
}
