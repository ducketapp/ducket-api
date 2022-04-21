package domain.model.operation

import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.imports.Import
import domain.model.imports.ImportEntity
import domain.model.imports.ImportsTable
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object OperationsTable : LongIdTable("operation") {
    val userId = reference("user_id", UsersTable)
    val categoryId = reference("category_id", CategoriesTable)
    val importId = optReference("import_id", ImportsTable)
    val description = varchar("description", 64).nullable()
    val subject = varchar("subject", 64).nullable()
    val notes = varchar("notes", 128).nullable()
    val latitude = decimal("latitude", 10, 7).nullable()
    val longitude = decimal("longitude", 10, 7).nullable()
    val date = timestamp("date")
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class OperationEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OperationEntity>(OperationsTable)

    var user by UserEntity referencedOn OperationsTable.userId
    var category by CategoryEntity referencedOn OperationsTable.categoryId
    var import by ImportEntity optionalReferencedOn OperationsTable.importId
    var description by OperationsTable.description
    var subject by OperationsTable.subject
    var notes by OperationsTable.notes
    var latitude by OperationsTable.latitude
    var longitude by OperationsTable.longitude
    var date by OperationsTable.date
    var createdAt by OperationsTable.createdAt
    var modifiedAt by OperationsTable.modifiedAt

    var attachments by AttachmentEntity via OperationAttachmentsTable

    fun toModel() = Operation(
        id = id.value,
        user = user.toModel(),
        category = category.toModel(),
        import = import?.toModel(),
        description = description,
        subject = subject,
        notes = notes,
        attachments = attachments.toList().map { it.toModel() },
        latitude = latitude,
        longitude = longitude,
        date = date,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )
}

data class Operation(
    val id: Long,
    val user: User,
    val category: Category,
    val import: Import?,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val attachments: List<Attachment>,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val date: Instant,
    val createdAt: Instant,
    val modifiedAt: Instant,
)