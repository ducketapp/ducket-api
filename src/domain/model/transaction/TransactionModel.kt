package domain.model.transaction

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
import io.ducket.api.domain.model.attachment.Attachment
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.transaction.TransactionAttachmentsTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object TransactionsTable : LongIdTable("transaction") {
    val userId = reference("user_id", UsersTable)
    val accountId = reference("account_id", AccountsTable)
    val categoryId = optReference("category_id", CategoriesTable)
    val importId = optReference("import_id", ImportsTable)
    val date = timestamp("date")
    val amount = decimal("amount", 10, 2)
    val payeeOrPayer = varchar("payee_or_payer", 128)
    val notes = varchar("notes", 128).nullable()
    val longitude = varchar("longitude", 45).nullable()
    val latitude = varchar("latitude", 45).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class TransactionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TransactionEntity>(TransactionsTable)

    var account by AccountEntity referencedOn TransactionsTable.accountId
    var user by UserEntity referencedOn TransactionsTable.userId
    var category by CategoryEntity optionalReferencedOn TransactionsTable.categoryId
    var import by ImportEntity optionalReferencedOn TransactionsTable.importId
    var amount by TransactionsTable.amount
    var date by TransactionsTable.date
    var payeeOrPayer by TransactionsTable.payeeOrPayer
    var notes by TransactionsTable.notes
    var longitude by TransactionsTable.longitude
    var latitude by TransactionsTable.latitude
    var createdAt by TransactionsTable.createdAt
    var modifiedAt by TransactionsTable.modifiedAt

    var attachments by AttachmentEntity via TransactionAttachmentsTable

    fun toModel() = Transaction(
        id.value,
        account.toModel(),
        category?.toModel(),
        user.toModel(),
        import?.toModel(),
        amount,
        date,
        payeeOrPayer,
        notes,
        longitude,
        latitude,
        attachments.toList().map { it.toModel() },
        createdAt,
        modifiedAt,
    )
}

class Transaction(
    val id: Long,
    val account: Account,
    val category: Category?,
    val user: User,
    val import: Import?,
    val amount: BigDecimal,
    val date: Instant,
    val payeeOrPayer: String,
    val notes: String?,
    val longitude: String?,
    val latitude: String?,
    val attachments: List<Attachment>,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
