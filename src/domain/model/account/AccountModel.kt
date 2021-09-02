package domain.model.account

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import domain.model.user.User
import domain.model.user.UserEntity
import domain.model.user.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant
import java.time.LocalDateTime

internal object AccountsTable : IntIdTable("account") {
    val name = varchar("name", 45)
    val notes = varchar("notes", 128).nullable()
    val userId = reference("user_id", UsersTable)
    val currencyId = reference("currency_id", CurrenciesTable)
    val accountTypeId = reference("account_type_id", AccountTypesTable)
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
}

class AccountEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AccountEntity>(AccountsTable)

    var name by AccountsTable.name
    var notes by AccountsTable.notes
    var user by UserEntity referencedOn AccountsTable.userId
    var currency by CurrencyEntity referencedOn AccountsTable.currencyId
    var accountType by AccountTypeEntity referencedOn AccountsTable.accountTypeId
    var createdAt by AccountsTable.createdAt
    var modifiedAt by AccountsTable.modifiedAt

    fun toModel() = Account(id.value, name, notes, user.toModel(), currency.toModel(), accountType.toModel(), createdAt, modifiedAt)
}

data class Account(
    val id: Int,
    val name: String,
    val notes: String?,
    val user: User,
    val currency: Currency,
    val type: AccountType,
    val createdAt: Instant,
    val modifiedAt: Instant,
)