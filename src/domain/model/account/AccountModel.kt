package domain.model.account

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import domain.model.transaction.TransactionsTable
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import io.ducket.api.app.AccountType
import io.ducket.api.domain.model.transfer.TransfersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object AccountsTable : LongIdTable("account") {
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val accountType = enumerationByName("account_type", 32, AccountType::class)
    val name = varchar("name", 64)
    val notes = varchar("notes", 128).nullable()
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class AccountEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AccountEntity>(AccountsTable)

    var name by AccountsTable.name
    var notes by AccountsTable.notes
    var user by UserEntity referencedOn AccountsTable.userId
    var currency by CurrencyEntity referencedOn AccountsTable.currencyId
    var accountType by AccountsTable.accountType
    var createdAt by AccountsTable.createdAt
    var modifiedAt by AccountsTable.modifiedAt

    private var transactionsAccount by AccountEntity via TransactionsTable
    private var transfersAccount by AccountEntity.via(TransfersTable.accountId, TransfersTable.accountId)

    fun toModel() = Account(
        id.value,
        name,
        notes,
        user.toModel(),
        currency.toModel(),
        accountType,
        (transactionsAccount + transfersAccount).count(),
        createdAt,
        modifiedAt,
    )
}

data class Account(
    val id: Long,
    val name: String,
    val notes: String?,
    val user: User,
    val currency: Currency,
    val type: AccountType,
    val recordsCount: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
