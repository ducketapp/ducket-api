package domain.model.user

import domain.model.currency.CurrenciesTable
import domain.model.currency.Currency
import domain.model.currency.CurrencyEntity
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object UsersTable : LongIdTable("user") {
    val phone = varchar("phone", 32).nullable().uniqueIndex()
    val email = varchar("email", 64).uniqueIndex()
    val name = varchar("name", 64)
    val passwordHash = varchar("password_hash", 128)
    val mainCurrencyId = reference("main_currency_id", CurrenciesTable)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UsersTable)

    var mainCurrency by CurrencyEntity referencedOn UsersTable.mainCurrencyId

    var phone by UsersTable.phone
    var name by UsersTable.name
    var email by UsersTable.email
    var passwordHash by UsersTable.passwordHash
    var createdAt by UsersTable.createdAt
    var modifiedAt by UsersTable.modifiedAt

    fun toModel() = User(
        id.value,
        phone,
        name,
        email,
        mainCurrency.toModel(),
        passwordHash,
        createdAt,
        modifiedAt
    )
}

data class User(
    val id: Long,
    val phone: String?,
    val name: String,
    val email: String,
    val mainCurrency: Currency,
    val passwordHash: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)
