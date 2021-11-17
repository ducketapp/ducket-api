package domain.model.account

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import domain.model.transaction.TransactionEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.domain.model.CombinedIdTable
import io.ducket.api.domain.model.StringIdTable
import io.ducket.api.domain.model.attachment.AttachmentEntity
import io.ducket.api.domain.model.attachment.AttachmentsTable
import io.ducket.api.domain.model.transaction.TransactionAttachmentsTable
import io.ducket.api.domain.model.transfer.TransfersTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant
import java.util.*

internal object AccountsTable : StringIdTable("account") {
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val accountType = enumerationByName("account_type", 32, AccountType::class)
    val name = varchar("name", 45)
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class AccountEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, AccountEntity>(AccountsTable)

    var name by AccountsTable.name
    var notes by AccountsTable.notes
    var user by UserEntity referencedOn AccountsTable.userId
    var currency by CurrencyEntity referencedOn AccountsTable.currencyId
    var accountType by AccountsTable.accountType
    var createdAt by AccountsTable.createdAt
    var modifiedAt by AccountsTable.modifiedAt

    private var transactions by AccountEntity via TransactionsTable
    private var transfers by AccountEntity.via(TransfersTable.accountId, TransfersTable.accountId)

    fun toModel() = Account(
        id.value,
        name,
        notes,
        user.toModel(),
        currency.toModel(),
        accountType,
        (transactions.count() + transfers.count()).toInt(),
        createdAt,
        modifiedAt,
    )
}

data class Account(
    val id: String,
    val name: String,
    val notes: String?,
    val user: User,
    val currency: Currency,
    val type: AccountType,
    val numOfRecords: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

enum class AccountType {
    GENERAL, DEBIT_CARD, CREDIT_CARD, CASH, BANK_ACCOUNT, SAVINGS
}