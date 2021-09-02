package io.budgery.api.domain.model.transfer

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
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

internal object TransfersTable : IntIdTable("transfer") {
    val accountId = reference("account_id", AccountsTable)
    val transferAccountId = reference("transfer_account_id", AccountsTable)
    val userId = reference("user_id", UsersTable)
    val categoryId = reference("category_id", CategoriesTable)
    val importId = optReference("import_id", ImportsTable)
    val relationUuid = varchar("transfer_relation_uuid", 36)
    val exchangeRate = double("exchange_rate").default(1.0)
    val amount = decimal("amount", 10, 2)
    val date = timestamp("date")
    val payee = varchar("payee", 128)
    val note = varchar("note", 128).nullable()
    val longitude = varchar("longitude", 45).nullable()
    val latitude = varchar("latitude", 45).nullable()
    val attachmentPath = varchar("attachment_path", 128).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class TransferEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransferEntity>(TransfersTable)

    var transferAccount by AccountEntity referencedOn TransfersTable.transferAccountId
    var account by AccountEntity referencedOn TransfersTable.accountId
    var user by UserEntity referencedOn TransfersTable.userId
    var category by CategoryEntity referencedOn TransfersTable.categoryId
    var import by ImportEntity optionalReferencedOn TransfersTable.importId
    var relationUuid by TransfersTable.relationUuid
    var exchangeRate by TransfersTable.exchangeRate
    var amount by TransfersTable.amount
    var date by TransfersTable.date
    var payee by TransfersTable.payee
    var note by TransfersTable.note
    var longitude by TransfersTable.longitude
    var latitude by TransfersTable.latitude
    var attachmentPath by TransfersTable.attachmentPath
    var createdAt by TransfersTable.createdAt
    var modifiedAt by TransfersTable.modifiedAt

    fun toModel() = Transfer(
        id.value,
        transferAccount.toModel(),
        account.toModel(),
        user.toModel(),
        category.toModel(),
        import?.toModel(),
        UUID.fromString(relationUuid),
        exchangeRate,
        amount,
        date,
        payee,
        note,
        longitude,
        latitude,
        attachmentPath,
        createdAt,
        modifiedAt,
    )
}

class Transfer(
    val id: Int,
    val transferAccount: Account,
    val account: Account,
    val user: User,
    val category: Category,
    val import: Import?,
    val relationUuid: UUID,
    val exchangeRate: Double,
    val amount: BigDecimal,
    val date: Instant,
    val payee: String,
    val note: String?,
    val longitude: String?,
    val latitude: String?,
    val attachmentPath: String?,
    val createdAt: Instant,
    val modifiedAt: Instant,
)