package io.budgery.api.domain.model.transaction

import domain.model.transaction.Transaction
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import io.budgery.api.domain.model.attachment.Attachment
import io.budgery.api.domain.model.attachment.AttachmentEntity
import io.budgery.api.domain.model.attachment.AttachmentsTable
import io.budgery.api.domain.model.label.Label
import io.budgery.api.domain.model.label.LabelEntity
import io.budgery.api.domain.model.label.LabelsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object TransactionAttachmentsTable : IntIdTable("transaction_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable)
    val transactionId = reference("transaction_id", TransactionsTable)
}

class TransactionAttachmentEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransactionAttachmentEntity>(TransactionAttachmentsTable)

    var attachment by AttachmentEntity referencedOn TransactionAttachmentsTable.attachmentId
    var transaction by TransactionEntity referencedOn TransactionAttachmentsTable.transactionId

    fun toModel() = TransactionAttachment(id.value, attachment.toModel(), transaction.toModel())
}

data class TransactionAttachment(
    val id: Int,
    val attachment: Attachment,
    val transaction: Transaction,
)