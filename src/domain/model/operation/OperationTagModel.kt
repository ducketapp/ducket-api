package dev.ducket.api.domain.model.operation

import dev.ducket.api.domain.model.tag.TagsTable
import org.jetbrains.exposed.sql.Table

internal object OperationTagsTable : Table("operation_tag") {
    val tagId = reference("tag_id", TagsTable.id)
    val operationId = reference("operation_id", OperationsTable.id)

    override val primaryKey = PrimaryKey(tagId, operationId)
}