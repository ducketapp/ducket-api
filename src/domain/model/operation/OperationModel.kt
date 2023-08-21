package org.expenny.service.domain.model.operation

import org.expenny.service.domain.model.user.User
import org.expenny.service.domain.model.user.UserEntity
import org.expenny.service.domain.model.user.UsersTable
import org.expenny.service.app.DEFAULT_SCALE
import org.expenny.service.app.OperationType
import org.expenny.service.domain.model.account.Account
import org.expenny.service.domain.model.account.AccountEntity
import org.expenny.service.domain.model.account.AccountsTable
import org.expenny.service.domain.model.category.CategoriesTable
import org.expenny.service.domain.model.category.Category
import org.expenny.service.domain.model.category.CategoryEntity
import org.expenny.service.domain.model.imports.Import
import org.expenny.service.domain.model.imports.ImportEntity
import org.expenny.service.domain.model.imports.ImportsTable
import org.expenny.service.domain.model.tag.Tag
import org.expenny.service.domain.model.tag.TagEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object OperationsTable : LongIdTable("operation") {
    val extId = varchar("ext_id", 128).nullable()
    val userId = reference("user_id", UsersTable)
    val categoryId = optReference("category_id", CategoriesTable)
    val importId = optReference("import_id", ImportsTable)
    val transferAccountId = reference("transfer_account_id", AccountsTable).nullable()
    val accountId = reference("account_id", AccountsTable)
    val type = enumerationByName("type", 32, OperationType::class)
    val clearedAmount = decimal("cleared_amount", 10, DEFAULT_SCALE)
    val postedAmount = decimal("posted_amount", 10, DEFAULT_SCALE)
    val date = timestamp("date")
    val description = varchar("description", 128).nullable()
    val subject = varchar("subject", 128).nullable()
    val notes = varchar("notes", 128).nullable()
    val latitude = decimal("latitude", 10, 7).nullable()
    val longitude = decimal("longitude", 10, 7).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }

    init {
        uniqueIndex("operation_unique_index", extId, userId)
    }
}

class OperationEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OperationEntity>(OperationsTable)

    var user by UserEntity referencedOn OperationsTable.userId
    var category by CategoryEntity optionalReferencedOn OperationsTable.categoryId
    var import by ImportEntity optionalReferencedOn OperationsTable.importId
    var transferAccount by AccountEntity optionalReferencedOn OperationsTable.transferAccountId
    var account by AccountEntity referencedOn OperationsTable.accountId

    var extId by OperationsTable.extId
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

    var tags by TagEntity via OperationTagsTable

    fun toModel() = Operation(
        id = id.value,
        extId = extId,
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
    val extId: String?,
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
    val extId: String?,
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
