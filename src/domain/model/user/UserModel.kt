package dev.ducketapp.service.domain.model.user

import dev.ducketapp.service.domain.model.currency.CurrenciesTable
import dev.ducketapp.service.domain.model.currency.Currency
import dev.ducketapp.service.domain.model.currency.CurrencyEntity
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

internal object UsersTable : LongIdTable("user") {
    val email = varchar("email", 64).uniqueIndex()
    val name = varchar("name", 64)
    val passwordHash = varchar("password_hash", 128)
    val currencyId = reference("currency_id", CurrenciesTable)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val modifiedAt = timestamp("modified_at").clientDefault { Instant.now() }
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UsersTable)

    var currency by CurrencyEntity referencedOn UsersTable.currencyId

    var name by UsersTable.name
    var email by UsersTable.email
    var passwordHash by UsersTable.passwordHash
    var createdAt by UsersTable.createdAt
    var modifiedAt by UsersTable.modifiedAt

    fun toModel() = User(
        id.value,
        name,
        email,
        currency.toModel(),
        passwordHash,
        createdAt,
        modifiedAt
    )
}

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val currency: Currency,
    val passwordHash: String,
    val createdAt: Instant,
    val modifiedAt: Instant,
)

data class UserCreate(
    val name: String,
    val email: String,
    val currency: String,
    val passwordHash: String,
)

data class UserUpdate(
    val name: String,
    val passwordHash: String,
)
