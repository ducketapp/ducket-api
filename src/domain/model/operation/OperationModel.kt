package domain.model.operation

import domain.model.account.Account
import domain.model.account.AccountEntity
import domain.model.account.AccountsTable
import domain.model.category.CategoriesTable
import domain.model.category.Category
import domain.model.category.CategoryEntity
import domain.model.imports.Import
import domain.model.imports.ImportEntity
import domain.model.imports.ImportsTable
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.app.DEFAULT_SCALE
import io.ducket.api.app.OperationType
import io.ducket.api.domain.model.operation.OperationTagsTable
import io.ducket.api.domain.model.tag.Tag
import io.ducket.api.domain.model.tag.TagEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object OperationsTable : LongIdTable("operation") {
    val userId = reference("user_id", UsersTable)
    val categoryId = optReference("category_id", CategoriesTable)
    val importId = optReference("import_id", ImportsTable)
    val transferAccountId = reference("transfer_account_id", AccountsTable).nullable()
    val accountId = reference("account_id", AccountsTable)
    val type = enumerationByName("type", 32, OperationType::class)
    val clearedAmount = decimal("cleared_amount", 10, DEFAULT_SCALE)
    val postedAmount = decimal("posted_amount", 10, DEFAULT_SCALE)
    val date = timestamp("date")
    val description = varchar("description", 64).nullable()
    val subject = varchar("subject", 64).nullable()
    val notes = varchar("notes", 128).nullable()
    val latitude = decimal("latitude", 10, 7).nullable()
    val longitude = decimal("longitude", 10, 7).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class OperationEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OperationEntity>(OperationsTable)

    var user by UserEntity referencedOn OperationsTable.userId
    var category by CategoryEntity optionalReferencedOn OperationsTable.categoryId
    var import by ImportEntity optionalReferencedOn OperationsTable.importId
    var transferAccount by AccountEntity optionalReferencedOn OperationsTable.transferAccountId
    var account by AccountEntity referencedOn OperationsTable.accountId

    var type by OperationsTable.type
    var clearedAmount by OperationsTable.clearedAmount
    var postedAmount by OperationsTable.postedAmount
    var date by OperationsTable.date
    var description by OperationsTable.description
    var subject by OperationsTable.subject
    var notes by OperationsTable.notes
    var latitude by OperationsTable.latitude
    var longitude by OperationsTable.longitude
    var createdAt by OperationsTable.createdAt
    var modifiedAt by OperationsTable.modifiedAt

    // var attachments by AttachmentEntity via OperationAttachmentsTable
    var tags by TagEntity via OperationTagsTable

    fun toModel() = Operation(
        id = id.value,
        user = user.toModel(),
        category = category?.toModel(),
        import = import?.toModel(),
        transferAccount = transferAccount?.toModel(),
        account = account.toModel(),
        type = type,
        clearedAmount = clearedAmount,
        postedAmount = postedAmount,
        date = date,
        description = description,
        subject = subject,
        notes = notes,
        latitude = latitude,
        longitude = longitude,
        tags = tags.toList().map { it.toModel() },
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )
}

data class Operation(
    val id: Long,
    val user: User,
    val category: Category?,
    val import: Import?,
    val transferAccount: Account?,
    val account: Account,
    val type: OperationType,
    val clearedAmount: BigDecimal,
    val postedAmount: BigDecimal,
    val date: Instant,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val tags: List<Tag>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class OperationCreate(
    val userId: Long,
    val categoryId: Long?,
    val importId: Long?,
    val transferAccountId: Long?,
    val accountId: Long,
    val type: OperationType,
    val clearedAmount: BigDecimal,
    val postedAmount: BigDecimal,
    val date: Instant,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
)

data class OperationUpdate(
    val categoryId: Long?,
    val transferAccountId: Long?,
    val accountId: Long,
    val type: OperationType,
    val clearedAmount: BigDecimal,
    val postedAmount: BigDecimal,
    val date: Instant,
    val description: String?,
    val subject: String?,
    val notes: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
)
