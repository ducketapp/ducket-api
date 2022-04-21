package domain.model.operation

import io.ducket.api.domain.model.attachment.AttachmentsTable
import org.jetbrains.exposed.sql.Table

internal object OperationAttachmentsTable : Table("operation_attachment") {
    val attachmentId = reference("attachment_id", AttachmentsTable.id)
    val operationId = reference("operation_id", OperationsTable.id)

    override val primaryKey = PrimaryKey(attachmentId, operationId)
}