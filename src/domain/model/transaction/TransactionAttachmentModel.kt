package io.ducket.api.domain.model.transaction

import domain.model.transaction.Transaction
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.label.Label
import io.ducket.api.domain.model.label.LabelEntity
import io.ducket.api.domain.model.label.LabelsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import java.util.*

internal object TransactionAttachmentsTable : Table("transaction_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable.id)
    val transactionId = reference("transaction_id", TransactionsTable.id)

    override val primaryKey = PrimaryKey(attachmentId, transactionId)
}

/*
class TransactionAttachmentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TransactionAttachmentEntity>(TransactionAttachmentsTable)

    var attachment by AttachmentEntity referencedOn TransactionAttachmentsTable.attachmentId
    var transaction by TransactionEntity referencedOn TransactionAttachmentsTable.transactionId

    fun toModel() = TransactionAttachment(id.value, attachment.toModel(), transaction.toModel())
}

data class TransactionAttachment(
    val id: UUID,
    val attachment: Attachment,
    val transaction: Transaction,
)*/
