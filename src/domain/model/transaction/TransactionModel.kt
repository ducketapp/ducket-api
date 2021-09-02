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
import io.budgery.api.domain.model.label.Label
import io.budgery.api.domain.model.label.LabelEntity
import io.budgery.api.domain.model.label.TransactionLabelsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.math.BigDecimal
import java.time.Instant

internal object TransactionsTable : IntIdTable("transaction") {
    val accountId = reference("account_id", AccountsTable)
    val categoryId = reference("category_id", CategoriesTable)
    val userId = reference("user_id", UsersTable)
    val importId = optReference("import_id", ImportsTable)
    val transactionRuleId = optReference("transaction_rule_id", TransactionRulesTable)
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

class TransactionEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransactionEntity>(TransactionsTable)

    var account by AccountEntity referencedOn TransactionsTable.accountId
    var category by CategoryEntity referencedOn TransactionsTable.categoryId
    var user by UserEntity referencedOn TransactionsTable.userId
    var import by ImportEntity optionalReferencedOn TransactionsTable.importId
    var transactionRule by TransactionRuleEntity optionalReferencedOn TransactionsTable.transactionRuleId
    var amount by TransactionsTable.amount
    var date by TransactionsTable.date
    var payee by TransactionsTable.payee
    var note by TransactionsTable.note
    var longitude by TransactionsTable.longitude
    var latitude by TransactionsTable.latitude
    var attachmentPath by TransactionsTable.attachmentPath
    var createdAt by TransactionsTable.createdAt
    var modifiedAt by TransactionsTable.modifiedAt

    var labels by LabelEntity via TransactionLabelsTable

    fun toModel() = Transaction(
        id.value,
        account.toModel(),
        category.toModel(),
        user.toModel(),
        import?.toModel(),
        transactionRule?.toModel(),
        labels.toList().map { it.toModel() },
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

class Transaction(
    val id: Int,
    val account: Account,
    val category: Category,
    val user: User,
    val import: Import?,
    val rule: TransactionRule?,
    val labels: List<Label>,
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